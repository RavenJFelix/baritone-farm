package baritone.process;

import baritone.utils.BaritoneProcessHelper;
import baritone.Baritone;
import baritone.cache.WorldScanner;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.MovementHelper;


import baritone.api.process.ITillProcess;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.*;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemHoe;

import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
public final class TillProcess extends BaritoneProcessHelper implements ITillProcess
{
	private boolean active;
	private List<BlockPos> locations;

	private int tickCount;

	/*private enum Tillable
	{
		PUMPKIN(Blocks.PUMPKIN, state->true),
		DIRT(Blocks.DIRT, state->true)
	};
	*/

	public TillProcess(Baritone baritone)
	{
		super(baritone);
	}

	@Override
	public boolean isActive() {return active;}

	@Override
	public void till()
	{

		active = true;
		locations = null;
	}
	@Override
	public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel)
	{
		ArrayList<Block> scan = new ArrayList<>();
		scan.add(Blocks.DIRT);

		if(Baritone.settings().mineGoalUpdateInterval.value != 0  && tickCount++ % Baritone.settings().mineGoalUpdateInterval.value == 0)
		{
			Baritone.getExecutor().execute(()->locations = WorldScanner.INSTANCE.scanChunkRadius(ctx, scan, 256, 10, 10));
		}
		
		if(locations == null)
		{
			return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
		}



		List<BlockPos> toTill = new ArrayList<>();
		for(BlockPos pos : locations)
		{
			IBlockState state = ctx.world().getBlockState(pos);
			boolean airAbove = ctx.world().getBlockState(pos.up()).getBlock() instanceof BlockAir ;
			if(state.getBlock() == Blocks.DIRT)
			{
				toTill.add(pos);
			}
		}

		baritone.getInputOverrideHandler().clearAllKeys();
		for(BlockPos pos : toTill)
		{
			Optional<Rotation> rot = RotationUtils.reachable(ctx, pos);
			if(rot.isPresent() && isSafeToCancel)
			{
				baritone.getLookBehavior().updateTarget(rot.get(), true);
				int hoeSlot = 0;
				for (int i = 0; i < 9; ++i)
				{
					ItemStack stack = ctx.player().inventory.getStackInSlot(i);
					if(stack.getItem() instanceof ItemHoe)
					{
						hoeSlot = i;
						break;
					}
				}
				ctx.player().inventory.currentItem = hoeSlot;
			}
		}


		return null;
	}
	@Override
	public String displayName0()
	{
		return "Tilling the fucking dirt.";
	}

	@Override
	public void onLostControl()
	{
		active = false;
	}
}

