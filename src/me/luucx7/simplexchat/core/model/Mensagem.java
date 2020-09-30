package me.luucx7.simplexchat.core.model;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.luucx7.simplexchat.SimplexChat;
import me.luucx7.simplexchat.core.api.Channel;
import me.luucx7.simplexchat.core.minedown.MineDown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Mensagem {

	final Player sender;
	String[] mensagem;
	BaseComponent[] mensagemFinal;
	Channel canal;

	public Mensagem(Player sender, String[] mensagem, Channel canal) {
		this.sender = sender;
		this.mensagem = mensagem;
		this.canal = canal;
	}

	public Mensagem preparar() {
		String formato = ChatColor.translateAlternateColorCodes('&', canal.getFormat());
		String playerMsg = mensagem[0];
		if (mensagem.length>1) {
			for (int i = 1;i<mensagem.length;i++) {
				playerMsg = playerMsg + " "+ mensagem[i];
			}
		}

		if (sender.hasPermission("chat.colored")) {
			playerMsg = ChatColor.translateAlternateColorCodes('&', playerMsg);
		} else {
			playerMsg = ChatColor.stripColor(playerMsg).replace("", "")
					.replace("show_entity=", "")
					.replace("show_item=", "");
		}

		formato = formato.replace("<message>", playerMsg)
				.replace("<player>", sender.getName()
				);

		mensagemFinal = MineDown.parse(PlaceholderAPI.setPlaceholders(sender, formato).replace("<br>", "\n"));
		return this;
	}

	public void enviar() {
		ArrayList<Player> recebedores = new ArrayList<Player>();
		
		if (canal.isBroadcast()) {
			Bukkit.getOnlinePlayers().stream().forEach(p -> recebedores.add(p));
		} else {
			int chanelRadius = canal.getRadius();
			Bukkit.getOnlinePlayers().stream().filter(p -> p.getLocation().getWorld().getName().equals(sender.getLocation().getWorld().getName())).filter(p -> p.getLocation().distance(sender.getLocation())<=chanelRadius).forEach(p -> recebedores.add(p));
		}
		
		if (canal.isRestrict()) {
			recebedores.stream().filter(r -> r.hasPermission(canal.getPermission())).forEach(r -> r.spigot().sendMessage(mensagemFinal));
		} else {
			recebedores.stream().forEach(r -> r.spigot().sendMessage(mensagemFinal));
		}
		
		if (canal.useActionbar()) {
			int quantia =  recebedores.size();
			String actionMessage = quantia>1 ? 
					ChatColor.translateAlternateColorCodes('&', SimplexChat.instance.getConfig().getString("amount_readed").replace("<amount>", (quantia-1)+""))
					: ChatColor.translateAlternateColorCodes('&', SimplexChat.instance.getConfig().getString("no_one"));
			
		   sender.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionMessage));
		}
		return;
	}
}
