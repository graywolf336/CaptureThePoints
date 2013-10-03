package me.dalton.capturethepoints;

/**
 *
 * @author pluckerpluck
 */

import java.io.Serializable;

import org.bukkit.Material;

public class MultiInvItem implements Serializable
{
    private static final long serialVersionUID = 2433424709013450693L;
    private Material itemMat = Material.AIR;
    private int quanitity = 0;
    private byte data = 0;
    private short durability = 0;

    public void setMaterial(Material mat) {
        itemMat = mat;
    }

    public void setQuanitity(Integer q) {
        quanitity = q;
    }

    public void setData(Byte d) {
        data = d;
    }

    public void setDurability(Short damage) {
        durability = damage;
    }

    public Material getMaterial() {
        return itemMat;
    }

    public int getQuanitity() {
        return quanitity;
    }

    public byte getData() {
        return data;
    }

    public short getDurability() {
        return durability;
    }

    @Override
    public String toString() {
        return itemMat + "," + quanitity + "," + data + "," + durability;
    }
    
    public void fromString(String string) {
        String[] split = string.split(",");
        if (split.length == 4) {
            setMaterial(Material.matchMaterial(split[0]));
            setQuanitity(Integer.parseInt(split[1]));
            setData(Byte.parseByte(split[2]));
            setDurability(Short.parseShort(split[3]));
        }
    }
}
