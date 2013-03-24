package me.dalton.capturethepoints;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Humsas
 */
public class Util {
	private CaptureThePoints ctp;
	
	public Util(CaptureThePoints ctp) {
		this.ctp = ctp;
	}
	
    public static final List<Material> WEAPONS_TYPE = new LinkedList<Material>();
    public static final List<Material> SWORDS_TYPE = new LinkedList<Material>();
    public static final List<Material> AXES_TYPE = new LinkedList<Material>();
    public static final List<Material> PICKAXES_TYPE = new LinkedList<Material>();
    public static final List<Material> SPADES_TYPE = new LinkedList<Material>();
    public static final List<Material> HOES_TYPE = new LinkedList<Material>();
    // Armor
    public static final List<Material> ARMORS_TYPE = new LinkedList<Material>();
    public static final List<Material> HELMETS_TYPE = new LinkedList<Material>();
    public static final List<Material> CHESTPLATES_TYPE = new LinkedList<Material>();
    public static final List<Material> LEGGINGS_TYPE = new LinkedList<Material>();
    public static final List<Material> BOOTS_TYPE = new LinkedList<Material>();

    static {
        // Weapons
        SWORDS_TYPE.add(Material.WOOD_SWORD);
        SWORDS_TYPE.add(Material.STONE_SWORD);
        SWORDS_TYPE.add(Material.GOLD_SWORD);
        SWORDS_TYPE.add(Material.IRON_SWORD);
        SWORDS_TYPE.add(Material.DIAMOND_SWORD);

        AXES_TYPE.add(Material.WOOD_AXE);
        AXES_TYPE.add(Material.STONE_AXE);
        AXES_TYPE.add(Material.GOLD_AXE);
        AXES_TYPE.add(Material.IRON_AXE);
        AXES_TYPE.add(Material.DIAMOND_AXE);

        PICKAXES_TYPE.add(Material.WOOD_PICKAXE);
        PICKAXES_TYPE.add(Material.STONE_PICKAXE);
        PICKAXES_TYPE.add(Material.GOLD_PICKAXE);
        PICKAXES_TYPE.add(Material.IRON_PICKAXE);
        PICKAXES_TYPE.add(Material.DIAMOND_PICKAXE);

        SPADES_TYPE.add(Material.WOOD_SPADE);
        SPADES_TYPE.add(Material.STONE_SPADE);
        SPADES_TYPE.add(Material.GOLD_SPADE);
        SPADES_TYPE.add(Material.IRON_SPADE);
        SPADES_TYPE.add(Material.DIAMOND_SPADE);

        HOES_TYPE.add(Material.WOOD_HOE);
        HOES_TYPE.add(Material.STONE_HOE);
        HOES_TYPE.add(Material.GOLD_HOE);
        HOES_TYPE.add(Material.IRON_HOE);
        HOES_TYPE.add(Material.DIAMOND_HOE);

        WEAPONS_TYPE.addAll(SWORDS_TYPE);
        WEAPONS_TYPE.addAll(AXES_TYPE);
        WEAPONS_TYPE.addAll(PICKAXES_TYPE);
        WEAPONS_TYPE.addAll(SPADES_TYPE);
        WEAPONS_TYPE.addAll(HOES_TYPE);

        // Armor
        HELMETS_TYPE.add(Material.LEATHER_HELMET);
        HELMETS_TYPE.add(Material.GOLD_HELMET);
        HELMETS_TYPE.add(Material.CHAINMAIL_HELMET);
        HELMETS_TYPE.add(Material.IRON_HELMET);
        HELMETS_TYPE.add(Material.DIAMOND_HELMET);
        HELMETS_TYPE.add(Material.PUMPKIN);

        CHESTPLATES_TYPE.add(Material.LEATHER_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.GOLD_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.CHAINMAIL_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.IRON_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.DIAMOND_CHESTPLATE);

        LEGGINGS_TYPE.add(Material.LEATHER_LEGGINGS);
        LEGGINGS_TYPE.add(Material.GOLD_LEGGINGS);
        LEGGINGS_TYPE.add(Material.CHAINMAIL_LEGGINGS);
        LEGGINGS_TYPE.add(Material.IRON_LEGGINGS);
        LEGGINGS_TYPE.add(Material.DIAMOND_LEGGINGS);

        BOOTS_TYPE.add(Material.LEATHER_BOOTS);
        BOOTS_TYPE.add(Material.GOLD_BOOTS);
        BOOTS_TYPE.add(Material.CHAINMAIL_BOOTS);
        BOOTS_TYPE.add(Material.IRON_BOOTS);
        BOOTS_TYPE.add(Material.DIAMOND_BOOTS);

        ARMORS_TYPE.addAll(HELMETS_TYPE);
        ARMORS_TYPE.addAll(CHESTPLATES_TYPE);
        ARMORS_TYPE.addAll(LEGGINGS_TYPE);
        ARMORS_TYPE.addAll(BOOTS_TYPE);
    }


    /** Helper method for equipping armor pieces. */
    public void equipArmorPiece(ItemStack stack, PlayerInventory inv) {
        Material type = stack.getType();

        if (HELMETS_TYPE.contains(type)) {
            inv.setHelmet(stack);
        } else if (CHESTPLATES_TYPE.contains(type)) {
            inv.setChestplate(stack);
        } else if (LEGGINGS_TYPE.contains(type)) {
            inv.setLeggings(stack);
        } else if (BOOTS_TYPE.contains(type)) {
            inv.setBoots(stack);
        }
    }

    /** Send message to Players that are playing in an arena
     * <p />
     * 
     * @param arena The arena to send the message to it's players.
     * @param message The message to send. "[CTP] " has been included.
     * @see PlayerData
     * @see Arena
     */
    public void sendMessageToPlayers(Arena arena, String message) {
        for (String player : arena.getPlayersData().keySet()) {
        	Player p = ctp.getServer().getPlayer(player);
            p.sendMessage(ChatColor.AQUA + "[CTP] " + ChatColor.WHITE + message); // Kj
        }
    }
    
    /** Send message to Players that are playing in the given arena but exclude a person.
     * <p />
     * 
     * @param arena The arena to send the message to it's players.
     * @param exclude The Player to exclude
     * @param s The message to send. "[CTP] " has been included.
     * @see PlayerData
     * @see Arena
     */
    public void sendMessageToPlayers(Arena arena, Player exclude, String s) {
        for (String player : arena.getPlayersData().keySet()) {
        	if(player.equalsIgnoreCase(exclude.getName())) continue;
        	
        	Player p = ctp.getServer().getPlayer(player);
            if (p != null)
                p.sendMessage(ChatColor.AQUA + "[CTP] " + ChatColor.WHITE + s); // Kj
        }
    }
    
    /**
     * Takes a comma-separated list of items in the <type>:<amount> format and
     * returns a list of ItemStacks created from that data.
     */
    public List<ItemStack> makeItemStackList(String string) {
        List<ItemStack> result = new LinkedList<ItemStack>();
        if (string == null || string.isEmpty()) {
            return result;
        }

        // Trim commas and whitespace, and split items by commas
        string = string.trim();
        if (string.endsWith(",")) {
            string = string.substring(0, string.length() - 1);
        }
        String[] items = string.split(",");

        for (String item : items) {
            // Trim whitespace and split by colons.
            item = item.trim();
            String[] parts = item.split(":");

            // Grab the amount.
            int amount = 1;
            if (parts.length == 1 && parts[0].matches("\\$[0-9]+")) {
                amount = Integer.parseInt(parts[0].substring(1, parts[0].length()));
            } else if (parts.length == 2 && parts[1].matches("(-)?[0-9]+")) {
                amount = Integer.parseInt(parts[1]);
            } else if (parts.length == 3 && parts[2].matches("(-)?[0-9]+")) // For dyes
            {
                amount = Integer.parseInt(parts[2]);
            }

            ItemStack stack = new ItemStack(0);
            // Make the ItemStack.
            if (amount > 64) {
                while (amount > 64) {
                    stack = (parts.length == 3)
                            ? makeItemStack(parts[0], amount, parts[1])
                            : makeItemStack(parts[0], amount);
                    amount -= 64;
                    if (stack != null) {
                        result.add(stack);
                    }
                }
            } else {
                stack = (parts.length == 3)
                        ? makeItemStack(parts[0], amount, parts[1])
                        : makeItemStack(parts[0], amount);
                if (stack != null) {
                    result.add(stack);
                }
            }
        }
        return result;
    }

    /** Helper methods for making ItemStacks out of strings and ints */
    public ItemStack makeItemStack(String name, int amount, String data) {
        try {
            byte offset = 0;

            Material material = (name.matches("[0-9]+"))
                    ? Material.getMaterial(Integer.parseInt(name))
                    : Material.valueOf(name.toUpperCase());

            if (material == Material.INK_SACK) {
                offset = 15;
            }

            DyeColor dye = (data.matches("[0-9]+"))
            		? DyeColor.getByDyeData((byte) Math.abs(offset - Integer.parseInt(data)))
                    : DyeColor.valueOf(data.toUpperCase());

            return new ItemStack(material, amount, (byte) Math.abs(offset - dye.getDyeData()));
        } catch (Exception e) {
            return null;
        }
    }

    /** Short for makeItemStack(name, amount, "0") */
    public ItemStack makeItemStack(String name, int amount) {
        return makeItemStack(name, amount, "0");
    }

    /** Returns whether String is a number. */
    public boolean isItInteger(String text) {
        @SuppressWarnings("unused")
		int id = 0;
        try {
            id = Integer.parseInt(text);
        } catch (Exception NumberFormatException) {
            return false;
        }
        return true;
    }

    //mine
    public List<Items> getItemListFromString(String text) {
        // Trim commas and whitespace, and split items by commas
        text = text.toUpperCase();
        text = text.trim();
        if (text.endsWith(",")) {
            text = text.substring(0, text.length() - 1);
        }
        String[] items = text.split(",");

        List<Items> list = new LinkedList<Items>();
        for (String item : items) {
            Items i = new Items();
            i.setAmount(1);
            i.setType((short) -1);
            
            // Trim whitespace
            item = item.trim();

            // Money
            getMoney(item, i);
            // Experience
            getExperience(item, i);

            if(i.getItem() != null && i.getItem().equals(Material.AIR)) {
                list.add(i);
                continue;
            }

            // Split by colons.
            String[] parts = item.split(":");
 
            // Enchanted items
            getEnchantments(parts, i);

            if (parts.length == 1) {
                if (isItInteger(parts[0])) {
                    i.setItem(Material.getMaterial(Integer.parseInt(parts[0])));
                } else {
                    i.setItem(Material.getMaterial(parts[0]));
                }
            } else if (parts.length == 2 && parts[1].matches("(-)?[0-9]+")) {
                if (isItInteger(parts[0])) {
                    i.setItem(Material.getMaterial(Integer.parseInt(parts[0])));
                    i.setAmount(Integer.parseInt(parts[1]));
                } else {
                    i.setItem(Material.getMaterial(parts[0]));
                    i.setAmount(Integer.parseInt(parts[1]));
                }
            } else if (parts.length == 3 && parts[2].matches("(-)?[0-9]+")) { // For dyes
                i.setAmount(Integer.parseInt(parts[2]));
                i.setType(Short.parseShort(parts[1]));
                if (isItInteger(parts[0]))
                    i.setItem(Material.getMaterial(Integer.parseInt(parts[0])));
                else
                    i.setItem(Material.getMaterial(parts[0]));
            }
            
            if (i.getItem() != null)
                list.add(i);
            else
                ctp.logWarning("Error while loading config file (Or Shop sign). Check: " + item);
        }
        return list;
    }

    public void getEnchantments(String[] enchantmentsString, Items item) {
        List<Enchantment> enchantments = new LinkedList<Enchantment>();
        List<Integer> enchLevels = new LinkedList<Integer>();
        try {
            for(int i = 0; i < enchantmentsString.length; i++) {
                String p = enchantmentsString[i];
                if(p.contains("/")) {
                    int firstLoc = p.indexOf("/");
                    String enchantString = p.substring(firstLoc + 1);
                    enchantmentsString[i] = p.substring(0, firstLoc);

                    String[] enchntParts = enchantString.split("/");
                    for(String ench : enchntParts) {
                        String[] enchParts = ench.split("-");
                        Enchantment enchantment;
                        int enchLevel = Integer.parseInt(enchParts[1]);

                        if(isItInteger(enchParts[0]))
                            enchantment = Enchantment.getById(Integer.parseInt(enchParts[0]));
                        else
                            enchantment = Enchantment.getByName(enchParts[0].toUpperCase());

                        if (enchLevel < 0 || enchLevel > enchantment.getMaxLevel())
                            enchLevel = enchantment.getMaxLevel();
                        
                        // Move to next enchantment
                        if(enchLevel == 0)
                            continue;

                        enchantments.add(enchantment);
                        enchLevels.add(enchLevel);
                    }
                }
            }
            item.setEnchantmentLevels(enchLevels);
            item.setEnchantments(enchantments);
        } catch(Exception e) {
        	ctp.getLogger().severe("Error while loading config file. Check: Item enchantments");
        }
    }

    public void getMoney(String string, Items item) {
        if(ctp.getEconomyHandler() == null)
            return;

        if(string.contains("$")) {
            item.setItem(Material.AIR);
            item.setMoney(Integer.parseInt(string.substring(string.indexOf("$") + 1)));
            string = "";
        }
    }

    public void getExperience(String string, Items item){
        if(string.contains("EXP")){
            item.setItem(Material.AIR);
            item.setExpReward(Integer.parseInt(string.substring(string.indexOf("EXP") + 4)));
            string = "";
        }
    }

	@SuppressWarnings("deprecation")
	public void rewardPlayer(Arena arena, Player player) {
		PlayerData pd = arena.getPlayerData(player.getName());
        try {
            player.giveExp(pd.getKills() * ctp.getRewards().getExpRewardForKillingEnemy());

            if (pd.isWinner()) {
                for (int i = 0; i < ctp.getRewards().getWinnerRewardCount(); i++) {
                    int itemCount = 0;
                    int id = random(0, ctp.getRewards().getWinnerRewards().size()); // Kj -- Took out -1
                    Items item = ctp.getRewards().getWinnerRewards().get(id);

                    // EXp + money
                    if(item.getItem().equals(Material.AIR)) {
                        if(ctp.getEconomyHandler() != null)
                        	ctp.getEconomyHandler().depositPlayer(player.getName(), item.getMoney());

                        player.giveExp(item.getExpReward());
                        continue;
                    }
                    
                    int amount = item.getAmount();
                    if (!(Util.ARMORS_TYPE.contains(item.getItem()) || Util.WEAPONS_TYPE.contains(item.getItem())))
                        for (ItemStack stack : player.getInventory().getContents())
                            if (stack != null && stack.getTypeId() == item.getItem().getId())
                                itemCount += stack.getAmount();

                    //player.sendMessage(player.getName() + " " + itemCount);
                    if (itemCount > 0)
                        player.getInventory().remove(item.getItem().getId());
                    
                    amount += itemCount;

                    ItemStack stack = new ItemStack(item.getItem(), amount);
                    if(item.getType() != -1)
                        stack.setDurability(item.getType());
                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++)
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));

                    player.getInventory().addItem(stack);
                }
            } else {
                for (int i = 0; i < ctp.getRewards().getOtherTeamRewardCount(); i++) {
                    int itemCount = 0;
                    int id = random(0, ctp.getRewards().getLooserRewards().size()); // Kj -- Took out -1
                    Items item = ctp.getRewards().getLooserRewards().get(id);
                    // EXp + money
                    if(item.getItem().equals(Material.AIR)) {
                        if(ctp.getEconomyHandler() != null)
                        	ctp.getEconomyHandler().depositPlayer(player.getName(), item.getMoney());

                        player.giveExp(item.getExpReward());
                        continue;
                    }
                    
                    int amount = item.getAmount();
                    if (!(Util.ARMORS_TYPE.contains(item.getItem()) || Util.WEAPONS_TYPE.contains(item.getItem())))
                        for (ItemStack stack : player.getInventory().getContents())
                            if (stack != null && stack.getTypeId() == item.getItem().getId())
                                itemCount += stack.getAmount();
                    
                    if (itemCount > 0)
                        player.getInventory().remove(item.getItem().getId());
                    
                    amount += itemCount;

                    ItemStack stack = new ItemStack(item.getItem(), amount);
                    if(item.getType() != -1)
                        stack.setDurability(item.getType());
                    
                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++)
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                    
                    player.getInventory().addItem(stack);
                }
            }
            //reward for kills
            for (int i = 0; i < pd.getKills(); i++) {
                if (ctp.getRewards().getRewardsForKill().size() > 0) {
                    int itemCount = 0;
                    int id = random(0, ctp.getRewards().getRewardsForKill().size()); // Kj -- Took out -1
                    Items item = ctp.getRewards().getRewardsForKill().get(id);
                    // EXp + money
                    if(item.getItem().equals(Material.AIR)) {
                        if(ctp.getEconomyHandler() != null)
                        	ctp.getEconomyHandler().depositPlayer(player.getName(), item.getMoney());

                        player.giveExp(item.getExpReward());
                        continue;
                    }
                    
                    int amount = item.getAmount();
                    if (!(Util.ARMORS_TYPE.contains(item.getItem()) || Util.WEAPONS_TYPE.contains(item.getItem())))
                        for (ItemStack stack : player.getInventory().getContents())
                            if (stack != null && stack.getTypeId() == item.getItem().getId())
                                itemCount += stack.getAmount();
                    
                    if (itemCount > 0)
                        player.getInventory().remove(item.getItem().getId());
                    
                    amount += itemCount;
                    ItemStack stack = new ItemStack(item.getItem(), amount);
                    if(item.getType() != -1)
                        stack.setDurability(item.getType());
                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++)
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));

                    player.getInventory().addItem(stack);
                }
            }
            
            //reward for capture
            for (int i = 0; i < pd.getPointsCaptured(); i++) {
                if (ctp.getRewards().getRewardsForCapture().size() > 0) {
                    int itemCount = 0;
                    int id = random(0, ctp.getRewards().getRewardsForCapture().size()); // Kj -- Took out -1
                    Items item = ctp.getRewards().getRewardsForCapture().get(id);
                    // EXp + money
                    if(item.getItem().equals(Material.AIR)) {
                        if(ctp.getEconomyHandler() != null)
                        	ctp.getEconomyHandler().depositPlayer(player.getName(), item.getMoney());

                        player.giveExp(item.getExpReward());
                        continue;
                    }
                    
                    int amount = item.getAmount();
                    if (!(Util.ARMORS_TYPE.contains(item.getItem()) || Util.WEAPONS_TYPE.contains(item.getItem())))
                        for (ItemStack stack : player.getInventory().getContents())
                            if ((stack != null) && (stack.getTypeId() == item.getItem().getId()))
                                itemCount += stack.getAmount();

                    if (itemCount > 0)
                        player.getInventory().remove(item.getItem().getId());

                    amount += itemCount;
                    ItemStack stack = new ItemStack(item.getItem(), amount);
                    if(item.getType() != -1)
                        stack.setDurability(item.getType());
                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++)
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                    
                    player.getInventory().addItem(stack);
                }
            }
            
            //It's deprecated but it's currently the only way to get the desired effect.
            player.updateInventory();
        } catch(Exception e) {
            ctp.logWarning("An error occured while rewarding " + player.getName());
        }

    }

    /** Generates a random number from startV to endV
     * @param startV Starting boundary
     * @param endV End boundary
     * @return A number generated in the boundary between startV to endV */
    public int random(int startV, int endV) { // Kj -- n must be positive checking
        if (endV > startV)
            return new Random().nextInt(endV) + startV;
        else if (startV > endV)
            return new Random().nextInt(startV) + endV;
        else //(startV == endV) 
            return startV;
    }

    /** Builds a vertical gate */
    public void buildVert(Player player, int start_x, int start_y, int start_z, int plusX, int plusY, int plusZ, int blockID) {
        for (int x = start_x; x < start_x + plusX; x++)
            for (int y = start_y; y < start_y + plusY; y++)
                for (int z = start_z; z < start_z + plusZ; z++)
                    player.getWorld().getBlockAt(x, y, z).setTypeId(blockID);
    }

    /** Removes a vertical point */
    public void removeVertPoint(Player player, String dir, int start_x, int start_y, int start_z, int blockID) {
        if (dir.equals("NORTH"))
            buildVert(player, start_x, start_y - 1, start_z - 1, 2, 4, 4, 0);
        else if (dir.equals("EAST"))
            buildVert(player, start_x - 1, start_y - 1, start_z, 4, 4, 2, 0);
        else if (dir.equals("SOUTH"))
            buildVert(player, start_x - 1, start_y - 1, start_z - 1, 2, 4, 4, 0);
        else if (dir.equals("WEST"))
            buildVert(player, start_x - 1, start_y - 1, start_z - 1, 4, 4, 2, 0);
    }

    /** Get the direction of facing from a Location's yaw. */
    public BlockFace getFace(Location loc) {
        BlockFace direction;
        double yaw = loc.getYaw();

        while (yaw < 0)
            yaw += 360;
        
        if ((yaw > 315) || (yaw <= 45))
            direction = BlockFace.WEST;
        else if ((yaw > 45) && (yaw <= 135))
            direction = BlockFace.NORTH;
        else if ((yaw > 135) && (yaw <= 225))
            direction = BlockFace.EAST;
        else
            direction = BlockFace.SOUTH;

        return direction;
    }
    
    /** 
     * Gets distance between two locations. World friendly.
     * @param loc1 The first Location
     * @param loc2 The second Location
     * @return Returns a double of the distance between them. Returns NaN if the Locations are not on the same World or distance is too great.
     */
     public double getDistance(Location loc1, Location loc2) { // Kjhf's
        if (loc1 != null && loc2 != null && loc1.getWorld() == loc2.getWorld())
            return loc1.distance(loc2);
        
        return Double.NaN;
    }

    // Checks if there is a team color in a list
    public boolean containsTeam(List<String> teams, String color) {
        for(String teamColor : teams)
            if(teamColor.equalsIgnoreCase(color))
                return true;

        return false;
    }
}