package net.h31ix.damage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Damage extends JavaPlugin implements Listener, CommandExecutor 
{
    Map<String,Double> damage = new HashMap<String,Double>();
    Map<String,Integer> xp = new HashMap<String,Integer>();
    
    @Override
    public void onDisable() 
    {
    }
    
    @Override
    public void onEnable() 
    {
        File file = new File(this.getDataFolder()+"/config.yml");
        if(!file.exists()) {
            this.saveDefaultConfig();
        }
        getServer().getPluginManager().registerEvents(this, this);
        List<String> list = getConfig().getStringList("damage");
        for(String string : list)
        {
            String [] s = string.split(":");
            damage.put(s[0],Double.parseDouble(s[1]));
        }  
        list = getConfig().getStringList("xp");
        for(String string : list)
        {
            String [] s = string.split(":");
            xp.put(s[0],Integer.parseInt(s[1]));
        }          
        getCommand("damage").setExecutor(this);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) 
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();
            double d = -1;
            for(String string : damage.keySet())
            {
                if(player.hasPermission(string))
                {
                    d = damage.get(string);
                    break;
                }
            }
            if(d != -1)
            {
                if(event.getEntity() instanceof Player && !(event.getDamager() instanceof Player) && event.getDamager() instanceof LivingEntity) {
                    event.setDamage((int)(d*event.getDamage()));
                }
                else if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && !(((Arrow)event.getDamager()).getShooter() instanceof Player) && ((Arrow)event.getDamager()).getShooter() instanceof LivingEntity) {
                    event.setDamage((int)(d*event.getDamage()));
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if(event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause().getEntity() != null)
        {
            Entity e = event.getEntity().getLastDamageCause().getEntity();
            if(e instanceof Player)
            {
                Player player = (Player)e;
                int d = -1;
                for(String string : xp.keySet())
                {
                    if(player.hasPermission(string))
                    {
                        d = xp.get(string);
                        break;
                    }
                }
                if(d != -1)
                {            
                    event.setDroppedExp((int)(event.getDroppedExp()*d));
                }
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) 
    {
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("reload"))
            {
                if(cs.hasPermission("damage.reload"))
                {
                    this.reloadConfig();
                    List<String> list = (List<String>)getConfig().getList("permissions");
                    for(String string : list)
                    {
                        String [] s = string.split(":");
                        damage.put(s[0],Double.parseDouble(s[1]));
                    } 
                    cs.sendMessage(ChatColor.GREEN+"Damage configuration reloaded.");
                }
            }
        }
        return true;
    }
}

