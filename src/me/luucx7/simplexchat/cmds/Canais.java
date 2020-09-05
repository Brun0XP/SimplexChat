package me.luucx7.simplexchat.cmds;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.core.CanaisManager;
import me.luucx7.simplexchat.core.model.Canal;
import me.luucx7.simplexchat.core.model.Mensagem;
import net.md_5.bungee.api.ChatColor;

public class Canais extends Command {

	String comando;
	
	public Canais(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender s, String comando, String[] args) {
		Optional<Canal> canalOp = CanaisManager.canaisCache.keySet().stream().filter(canal -> canal.getComando().equalsIgnoreCase(comando)).findFirst();
		if (!canalOp.isPresent()) {
			return true;
		}
		
		Canal canal = canalOp.get();
		
		if (canal.isRestrito()) {
			if (!s.hasPermission(canal.getPermissao())) {
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', SimplexChat.instance.getConfig().getString("no_permission")));
				return true;
			}
		}
		
		if (args.length==0) {
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', SimplexChat.instance.getConfig().getString("channel_usage_message").replace("{command}", comando.toLowerCase())));
			return true;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(SimplexChat.instance, () -> {
			Mensagem msg = new Mensagem((Player) s, args, canal.getNome());
			msg.preparar();
			msg.enviar();
		});
		return false;
	}

}
