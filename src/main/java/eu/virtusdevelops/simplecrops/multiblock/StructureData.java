package eu.virtusdevelops.simplecrops.multiblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

/**
 * Cross-version wrapper for block data - {@link BlockData} in 1.13+, {@link MaterialData} otherwise
 * @author Redempt
 *  https://github.com/Redempt/RedLib
 */
public class StructureData {
	
	private BlockData data;
	private MaterialData mdata;
	
	protected StructureData(String string) {

		data = Bukkit.createBlockData(string);

	}
	
	public StructureData(Material type) {
		data = type.createBlockData();
	}
	
	/**
	 * Creates a StructureData from a BlockData, for 1.13+
	 * @param data The BlockData
	 */
	public StructureData(BlockData data) {
		this.data = data;
	}
	
	public StructureData getRotated(Rotator rotator) {
		return new StructureData(rotator.rotate(data));
	}
	
	/**
	 * Creates a StructureData from a Material and byte data, for 1.12 and below
	 * @param type The block type
	 * @param data The data byte
	 */
	public StructureData(Material type, byte data) {
		mdata = new MaterialData(type, data);
	}
	
	/**
	 * Sets this StructureData at the given location
	 * @param block The block to set
	 */
	public void setBlock(Block block) {
		block.setBlockData(data, false);

	}
	
	/**
	 * Gets the BlockState to set for a given block
	 * @param block The Block to get the BlockState at
	 * @return The BlockState that would be set
	 */
	public BlockState getState(Block block) {
		BlockState state = block.getState();
		state.setBlockData(data);
		return state;
	}
	
	/**
	 * Sends a fake block change to a Player
	 * @param player The Player to send the fake block change to
	 * @param loc The Location of the fake block
	 */
	public void sendBlock(Player player, Location loc) {
		player.sendBlockChange(loc, data);
	}
	
	/**
	 * @return Whether the Material is air - for 1.15+, AIR, CAVE_AIR, or VOID_AIR
	 */
	public boolean isAir() {
		return getType() == Material.AIR;
	}
	
	/**
	 * @return The type of this StructureData
	 */
	public Material getType() {
		return data.getMaterial();
	}
	
	/**
	 * Compares this StructureData to a Block
	 * @param block The Block to compare with
	 * @param strict Whether to compare strictly
	 * @param ignoreAir Whether to return true automatically if this StructureData is air
	 * @return Whether the block matches this StructureData within the given parameters
	 */
	public boolean compare(Block block, boolean strict, boolean ignoreAir) {
		if (ignoreAir && isAir()) {
			return true;
		}
		if (!strict) {
			return block.getType() == getType();
		}
		return block.getBlockData().matches(data);
	}
	
}
