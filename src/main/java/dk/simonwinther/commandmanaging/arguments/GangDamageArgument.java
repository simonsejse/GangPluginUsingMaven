package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

public class GangDamageArgument implements CommandArguments
{
    private final GangManaging gangManaging;
    private final MessageProvider mp;

    public GangDamageArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.mp = plugin.getMessageProvider();
    }
    @Override
    public String getAlias()
    {
        return "damage";
    }

    @Override
    public String getArgument()
    {
        return "dm";
    }

    @Override
    public String usage()
    {
        return "/bande damage";
    }

    @Override
    public void perform(Player p, String... args)
    {
        final boolean value = gangManaging.damageMap.get(p.getUniqueId());
        gangManaging.damageMap.compute(p.getUniqueId(), (uuid, bool) -> !value);
        p.sendMessage(this.mp.toggleDamage.replace("{value}", (!value ? "&aTil" : "&cFra")));
    }
}
