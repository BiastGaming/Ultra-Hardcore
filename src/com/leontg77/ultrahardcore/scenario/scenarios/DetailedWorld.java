package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.leontg77.ultrahardcore.scenario.GeneratorScenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * DetailedWorld scenario class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class DetailedWorld extends GeneratorScenario {

    public DetailedWorld() {
        super(
            "DetailedWorld", 
            "The world has a lot of terrain changes from grass being mixed in with green clay to leaves having more details to them.", 
            ChatColor.GREEN, 
            "detail", 
            false, 
            false,
            true,
            true
        );
    }

    @Override
    public void handleBlock(Block block) {
        if (block.getType() == Material.DIRT && BlockUtils.getDurability(block) == 0 && rand.nextInt(8) == 0) {
            block.setData((byte) 1);
        }

        if (block.getType() == Material.LEAVES && rand.nextInt(8) == 0) {
            block.setData((byte) 1);
        }

        if (block.getType() == Material.LEAVES_2 && rand.nextInt(8) == 0) {
            block.setType(Material.LEAVES);
            block.setData((byte) 1);
        }
        
        if (block.getType() == Material.SAND && BlockUtils.getDurability(block) == 0 && rand.nextInt(8) == 0) {
            if (rand.nextBoolean()) {
                block.setType(Material.SANDSTONE);
            } else {
                block.setType(Material.SANDSTONE_STAIRS);
                block.setData((byte) rand.nextInt(4));
            }
        }
        
        if (block.getType() == Material.ICE && rand.nextInt(8) == 0) {
            block.setType(Material.PACKED_ICE);
        }
        
        if (block.getType() == Material.STONE && rand.nextInt(8) == 0) {
            block.setType(Material.COBBLESTONE);
        }

        if (block.getType() == Material.STONE && rand.nextInt(8) == 0) {
            block.setType(Material.STAINED_CLAY);
            block.setData((byte) 9);
        }
        
        if (block.getType() == Material.ICE && rand.nextInt(8) == 0) {
            block.setType(Material.PACKED_ICE);
        }

        if (block.getType() == Material.SNOW) {
            block.setData((byte) rand.nextInt(4));
        }

        if (block.getType() == Material.GRASS && rand.nextInt(50) < 14) {
            block.setType(Material.STAINED_CLAY);
            block.setData((byte) (rand.nextBoolean() ? 5 : 13));
        }

        if (block.getLocation().getBlockY() > 60) {
            if (block.getType() == Material.STONE && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                block.setType(Material.STAINED_GLASS);
                block.setData((byte) 8);
                
                block.getRelative(BlockFace.DOWN).setType(Material.GLOWSTONE);
            }

            if (block.getType() == Material.DIRT && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                block.setType(Material.STAINED_GLASS);
                block.setData((byte) 12);
                
                block.getRelative(BlockFace.DOWN).setType(Material.GLOWSTONE);
            }

            if (block.getType() == Material.GRASS && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                block.setType(Material.STAINED_GLASS);
                block.setData((byte) 13);
                
                block.getRelative(BlockFace.DOWN).setType(Material.GLOWSTONE);
            }
        }

        if (LocationUtils.hasBlockNearby(Material.LAVA, block.getLocation()) && rand.nextInt(3) == 0 && !block.isLiquid()) {
            block.setType(rand.nextBoolean() ? Material.NETHER_BRICK : Material.SOUL_SAND);
        }

        if (LocationUtils.hasBlockNearby(Material.LOG, block.getLocation()) && rand.nextInt(8) == 0 && block.isEmpty()) {
            block.setType(Material.SPRUCE_FENCE);
        }

        if (LocationUtils.hasBlockNearby(Material.LOG_2, block.getLocation()) && rand.nextInt(8) == 0 && block.isEmpty()) {
            block.setType(Material.SPRUCE_FENCE);
        }
        
        switch (block.getBiome()) {
        case BIRCH_FOREST:
        case BIRCH_FOREST_HILLS:
        case BIRCH_FOREST_HILLS_MOUNTAINS:
        case BIRCH_FOREST_MOUNTAINS:
            if (block.getType() == Material.SPRUCE_FENCE) {
                block.setType(Material.BIRCH_FENCE);
            }
            break;
        case EXTREME_HILLS:
        case EXTREME_HILLS_MOUNTAINS:
        case EXTREME_HILLS_PLUS:
        case EXTREME_HILLS_PLUS_MOUNTAINS:
        case MUSHROOM_ISLAND:
        case MUSHROOM_SHORE:
            if (block.getType() == Material.GRASS && rand.nextInt(8) == 0) {
                block.setType(Material.DIRT);
                block.setData((byte) 1);
            }
            break;
        case JUNGLE:
        case JUNGLE_EDGE:
        case JUNGLE_EDGE_MOUNTAINS:
        case JUNGLE_HILLS:
        case JUNGLE_MOUNTAINS:
        case SWAMPLAND:
        case SWAMPLAND_MOUNTAINS:
            if (block.getType() == Material.GRASS && rand.nextInt(8) == 0) {
                block.setType(Material.DIRT);
                block.setData((byte) 2);
            }
            
            if (block.getType() == Material.STAINED_CLAY && block.getLocation().getBlockY() > 60) {
                block.setType(Material.GRASS);
            }
            break;
        case SAVANNA:
        case SAVANNA_MOUNTAINS:
        case SAVANNA_PLATEAU:
        case SAVANNA_PLATEAU_MOUNTAINS:
            if (block.getType() == Material.LEAVES_2 || block.getType() == Material.LEAVES) {
                break;
            }
            
            if (!block.isEmpty() && !block.isLiquid() && rand.nextInt(1000) == 0) {
                Block up = block.getRelative(BlockFace.UP);
                
                if (up.isEmpty()) {
                    up.setType(Material.FIRE);
                    
                    block.setType(Material.NETHERRACK);

                    up.getRelative(BlockFace.NORTH).setType(Material.STEP);
                    up.getRelative(BlockFace.NORTH).setData((byte) 3);

                    up.getRelative(BlockFace.EAST).setType(Material.STEP);
                    up.getRelative(BlockFace.EAST).setData((byte) 3);

                    up.getRelative(BlockFace.WEST).setType(Material.STEP);
                    up.getRelative(BlockFace.WEST).setData((byte) 3);

                    up.getRelative(BlockFace.SOUTH).setType(Material.STEP);
                    up.getRelative(BlockFace.SOUTH).setData((byte) 3);
                }
            }
            break;
        case SUNFLOWER_PLAINS:
        case PLAINS:
            if (block.getType() == Material.LONG_GRASS || block.getType() == Material.DOUBLE_PLANT || block.getType() == Material.YELLOW_FLOWER || block.getType() == Material.RED_ROSE) {
                break;
            }
            
            if (!block.isEmpty() && !block.isLiquid() && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                if (rand.nextBoolean()) {
                    block.getRelative(BlockFace.UP).setType(Material.WOOD_STEP);
                } else {
                    block.getRelative(BlockFace.UP).setType(Material.FENCE);
                    
                    if (rand.nextBoolean()) {
                        block.getRelative(BlockFace.UP, 2).setType(Material.WOOD_STEP);
                    }
                }
            }
            break;
        default:
            break;
        }
    }
}