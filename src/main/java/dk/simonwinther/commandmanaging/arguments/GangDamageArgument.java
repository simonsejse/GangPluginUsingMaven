package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.entity.Player;

public class GangDamageArgument implements CommandArguments
{

    private final MainPlugin plugin;
    private GangManaging gangManaging;

    public GangDamageArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
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
        p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().TOGGLE_DAMAGE.replace("{value}", (!value ? "&aTil" : "&cFra"))));
    }
}
