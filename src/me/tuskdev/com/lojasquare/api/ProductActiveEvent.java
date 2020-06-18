package me.tuskdev.com.lojasquare.api;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import me.tuskdev.com.lojasquare.objects.Item;

public class ProductActiveEvent extends AbstractEvent implements Cancellable {
	
	// Fields
	private Player player;
	private Item item;
	private boolean cancelled;
	private Cause cause;
	
	// Constructor
	public ProductActiveEvent(Player player, Item item, Cause cause) {
		super();
		this.player = player;
		this.item = item;
		this.cancelled = false;
		this.cause = cause;
	}
	
	// Get the item of event
	public Item getItem() {
		return this.item;
	}
	
	// Verify if is cancelled event
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	// Set the cancelled status of event
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	// Get the cause of event
	@Override
	public Cause getCause() {
		return this.cause;
	}
	
	// Get the player of event
	public Player getPlayer() {
		return this.player;
	}
	
}
