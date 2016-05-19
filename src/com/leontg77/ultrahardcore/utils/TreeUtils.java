package com.leontg77.ultrahardcore.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Tree utilities class.
 * 
 * @author LeonTG77
 */
public class TreeUtils {

    /**
     * Get the TreeType for the given leaf/oak with the given durability
     *
     * @param type The type to get for.
     * @param durability The durability to get for.
     * @return The tree type, null if not a log or a leaf.
     */
    public static TreeType getTree(final Material type, final int durability) {
        switch (type) {
        case LOG:
        case LEAVES:
            switch (durability) {
            case 0:
            case 4:
            case 8:
            case 12:
                return TreeType.OAK;
            case 1:
            case 5:
            case 9:
            case 13:
                return TreeType.SPRUCE;
            case 2:
            case 6:
            case 10:
            case 14:
                return TreeType.BIRCH;
            case 3:
            case 7:
            case 11:
            case 15:
                return TreeType.JUNGLE;
            }
            break;
        case LEAVES_2:
        case LOG_2:
            switch (durability) {
            case 0:
            case 4:
            case 8:
            case 12:
                return TreeType.ACACIA;
            case 1:
            case 5:
            case 9:
            case 13:
                return TreeType.DARK_OAK;
            }
            break;
        default:
            break;
        }

        return TreeType.UNKNOWN;
    }

    /**
     * Tree type enum class.
     *
     * @author LeonTG77
     */
    public enum TreeType {
        /**
         * Represents a oak tree.
         */
        OAK(itemStack(Material.LOG, 0), itemStack(Material.LEAVES, 0), itemStack(Material.SAPLING, 0)),
        /**
         * Represents a spruce tree.
         */
        SPRUCE(itemStack(Material.LOG, 1), itemStack(Material.LEAVES, 1), itemStack(Material.SAPLING, 1)),
        /**
         * Represents a birch tree.
         */
        BIRCH(itemStack(Material.LOG, 2), itemStack(Material.LEAVES, 2), itemStack(Material.SAPLING, 2)),
        /**
         * Represents a jungle tree.
         */
        JUNGLE(itemStack(Material.LOG, 3), itemStack(Material.LEAVES, 3), itemStack(Material.SAPLING, 3)),
        /**
         * Represents an acacia tree.
         */
        ACACIA(itemStack(Material.LOG_2, 0), itemStack(Material.LEAVES_2, 0), itemStack(Material.SAPLING, 4)),
        /**
         * Represents a dark oak tree.
         */
        DARK_OAK(itemStack(Material.LOG_2, 1), itemStack(Material.LEAVES_2, 1), itemStack(Material.SAPLING, 5)),
        /**
         * Represents an unknown tree.
         */
        UNKNOWN(null, null, null);

        private ItemStack log, leaf, sapling;

        /**
         * TreeType constuctor
         *
         * @param log The log itemstack of the tree.
         * @param leaf The leaf itemstack of the tree.
         * @param sapling The sapling itemstack of the tree.
         */
        private TreeType(final ItemStack log, final ItemStack leaf, final ItemStack sapling) {
            this.sapling = sapling;
            this.leaf = leaf;
            this.log = log;
        }

        /**
         * Get the log itemstack for the tree type.
         *
         * @return The log ItemStack.
         */
        public ItemStack getLog() {
            return log;
        }

        /**
         * Get the leaf itemstack for the tree type.
         *
         * @return The leaf ItemStack.
         */
        public ItemStack getLeaf() {
            return leaf;
        }

        /**
         * Get the sapling itemstack for the tree type.
         *
         * @return The sapling ItemStack.
         */
        public ItemStack getSapling() {
            return sapling;
        }

        /**
         * Create an itemstack with the given type and durability
         * <p>
         * This is for making one fast without having to put the amount.
         *
         * @param type The type of the item.
         * @param durability The durability of the item.
         * @return The created itemstack.
         */
        private static ItemStack itemStack(Material type, int durability) {
            return new ItemStack(type, 1, (short) durability);
        }
    }
}