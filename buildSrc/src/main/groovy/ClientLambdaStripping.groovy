import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * Fabric Loader strips members annotated with @Environment(CLIENT) on dedicated servers, but not the synthetic lambda
 * methods that javac generates for lambdas inside them. Those keep references to client classes, which the bytecode
 * verifier can load on the server. This marks such lambda methods (transitively) with the same annotation.
 */
class ClientLambdaStripping {

    static final String ENVIRONMENT = 'Lnet/fabricmc/api/Environment;'
    static final String ENV_TYPE = 'Lnet/fabricmc/api/EnvType;'

    static int process(File classesDir) {
        int changedClasses = 0
        classesDir.eachFileRecurse { File file ->
            if (!file.name.endsWith('.class')) {
                return
            }
            ClassNode node = new ClassNode()
            new ClassReader(file.bytes).accept(node, 0)
            if (process(node)) {
                ClassWriter writer = new ClassWriter(0)
                node.accept(writer)
                file.bytes = writer.toByteArray()
                changedClasses++
            }
        }
        return changedClasses
    }

    static boolean isClient(List<AnnotationNode> annotations) {
        return annotations != null && annotations.any { a ->
            a.desc == ENVIRONMENT && a.values != null && a.values.size() >= 2 && a.values[1] instanceof String[]
                    && ((String[]) a.values[1])[1] == 'CLIENT'
        }
    }

    static boolean process(ClassNode node) {
        Map<String, MethodNode> lambdas = [:]
        node.methods.each { MethodNode m ->
            if ((m.access & Opcodes.ACC_SYNTHETIC) != 0 && m.name.startsWith('lambda$')) {
                lambdas[m.name + m.desc] = m
            }
        }
        if (lambdas.isEmpty()) {
            return false
        }
        Deque<MethodNode> queue = new ArrayDeque<>(node.methods.findAll { isClient(it.visibleAnnotations) || isClient(it.invisibleAnnotations) })
        Set<MethodNode> marked = new HashSet<>()
        boolean changed = false
        while (!queue.isEmpty()) {
            MethodNode method = queue.poll()
            method.instructions.each { insn ->
                List<String> targets = []
                if (insn instanceof InvokeDynamicInsnNode) {
                    insn.bsmArgs.each { arg ->
                        if (arg instanceof Handle && arg.owner == node.name) {
                            targets << (arg.name + arg.desc)
                        }
                    }
                } else if (insn instanceof MethodInsnNode && insn.owner == node.name) {
                    targets << (insn.name + insn.desc)
                }
                targets.each { key ->
                    MethodNode lambda = lambdas[key]
                    if (lambda != null && !marked.contains(lambda)) {
                        marked << lambda
                        if (!isClient(lambda.visibleAnnotations) && !isClient(lambda.invisibleAnnotations)) {
                            // @Environment has class retention
                            AnnotationNode annotation = new AnnotationNode(ENVIRONMENT)
                            annotation.visitEnum('value', ENV_TYPE, 'CLIENT')
                            if (lambda.invisibleAnnotations == null) {
                                lambda.invisibleAnnotations = []
                            }
                            lambda.invisibleAnnotations << annotation
                            changed = true
                        }
                        queue << lambda
                    }
                }
            }
        }
        return changed
    }
}
