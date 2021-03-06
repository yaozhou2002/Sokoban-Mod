package sokobanMod.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;

/**
 * Sokoban Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class TileEntityLootGenerator extends TileEntity{
    private final List<ItemStack> iStackRewards;
    private int achievement;

    public TileEntityLootGenerator(){
        iStackRewards = new ArrayList<ItemStack>();
        achievement = 0;
    }

    public void addLoot(ItemStack iStack){
        iStackRewards.add(iStack);
    }

    public void setAchievement(int achieve){
        achievement = achieve + 1;
    }

    public List<ItemStack> getLoot(){
        return iStackRewards;
    }

    public void giveLoot(World world){
        if(!world.isRemote) {
            /*
             * if(achievement != 0){ EntityAchievementOrb orb = new
             * EntityAchievementOrb(world, (double)this.xCoord +
             * 0.5D,(double)this.yCoord + 0.5D,(double)this.zCoord + 0.5D,
             * achievement - 1); world.spawnEntityInWorld(orb); }
             */
            for(int i = 0; i < iStackRewards.size(); i++) {
                ItemStack iStackLoot = iStackRewards.get(i);
                EntityItem entityLoot = new EntityItem(world, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, iStackLoot);
                world.spawnEntityInWorld(entityLoot);

            }
            Random rand = new Random();
            double spreading = 0.5D;
            for(int j = 0; j < 10; j++) {
                spawnParticle("explode", xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, (rand.nextDouble() - 0.5D) * spreading, (rand.nextDouble() - 0.5D) * spreading, (rand.nextDouble() - 0.5D) * spreading);
            }
        }

    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound){
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("achievement", achievement);
        NBTTagList var2 = new NBTTagList();
        par1NBTTagCompound.setInteger("rewardAmount", iStackRewards.size());
        for(int i = 0; i < iStackRewards.size(); i++) {
            if(iStackRewards.get(i) != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)i);
                iStackRewards.get(i).writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
        int rewardAmount;
        super.readFromNBT(par1NBTTagCompound);
        achievement = par1NBTTagCompound.getInteger("achievement");
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        rewardAmount = par1NBTTagCompound.getInteger("rewardAmount");
        for(int i = 0; i < rewardAmount; i++) {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(i);
            //int var5 = var4.getByte("Slot") & 255;
            iStackRewards.add(ItemStack.loadItemStackFromNBT(var4));
        }
    }

    private void spawnParticle(String string, double g, double h, double i, double d, double e, double f){
        PacketDispatcher.sendPacketToAllPlayers(PacketHandlerSokoban.spawnParticle(string, g, h, i, d, e, f));
    }
}
