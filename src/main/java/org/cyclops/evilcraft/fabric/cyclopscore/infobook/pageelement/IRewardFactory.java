package org.cyclops.evilcraft.fabric.cyclopscore.infobook.pageelement;



import org.cyclops.evilcraft.fabric.cyclopscore.infobook.IInfoBook;
import org.cyclops.evilcraft.fabric.cyclopscore.infobook.InfoBookParser;
import org.w3c.dom.Element;

/**
 * Factory for {@link IReward}.
 * @author rubensworks
 */
public interface IRewardFactory {
    public IReward create(IInfoBook infoBook, Element node) throws InfoBookParser.InvalidAppendixException;
}
