package baritone.process;

import baritone.api.process.INetherLoopProcess;

import baritone.utils.BaritoneProcessHelper;
import baritone.Baritone;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.cache.WorldScanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

import baritone.api.pathing.goals.Goal;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.pathing.goals.GoalComposite;
import baritone.pathing.movement.MovementHelper;
import baritone.api.utils.BlockZone;
import baritone.api.utils.math.MathHelper;


import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.material.MaterialPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.BlockPortal;

import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Collection;
public final class NetherLoopProcess extends BaritoneProcessHelper implements INetherLoopProcess
{

	private List <BlockPos>portalFrameObsidian = new ArrayList<>();
	private BlockOptionalMetaLookup filter;

	private List<BlockPos> obsidianLocations = new ArrayList<>();
	private final int MINE_FIELD_RADIUS = 5;
	private int tickCount = 0;
	private boolean active = false;
	private BlockPos netherEntryPoint;
	private BlockPos netherExitPoint;
	private BlockPos portalMinePoint;
	private PathingCommand pathingCommand = null;
	private BlockPos preMinePos = null;

	private BlockZone mineZone;

	private enum Objective
	{
		NETHER_ENTRY,
		NETHER_EXIT,
		PRE_MINE_INIT,
		PRE_MINE,
		MINE_PORTAL,
		MINE
	};

	private Objective objective = null;

	@Override
	public void setNetherEntryPoint(BlockPos pos)
	{
		netherEntryPoint = pos;
	}
	@Override
	public void setPortalMinePoint(BlockPos pos)
	{
		portalMinePoint = pos;
	}

	@Override
	public void setNetherExitPoint(BlockPos pos)
	{
		netherExitPoint = pos;
	}



	public NetherLoopProcess(Baritone baritone)
	{
		super(baritone);
		List<BlockOptionalMeta> filterStuff = new ArrayList<>();
		filterStuff.add(0, new BlockOptionalMeta(Blocks.OBSIDIAN));
		filter = new BlockOptionalMetaLookup(filterStuff.toArray(new BlockOptionalMeta[0]));
	}

	@Override
	public boolean isActive() {return active;}

	@Override
	public void netherLoop()
	{
		logDirect("FUCK LOOP YEAH");
		active = true;
		objective = Objective.NETHER_ENTRY;
		/*j
		  for (IWaypoint waypoint : waypoints)
		  {
		  if(waypoint.getName().equals("nether_entry"))
		  {
		  baritone.getNetherLoopProcess().setNetherEntryPoint(waypoint.getLocation());
		  }
		  else if(waypoint.getName().equals("nether_exit"))
		  {
		  baritone.getNetherLoopProcess().setNetherExitPoint(waypoint.getLocation());
		  }
		  }
		  */

	}



	@Override 
	public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel)
	{
		//printIfPortalBlock();
		switch(objective)
		{
			case NETHER_EXIT:
				netherExit(isSafeToCancel);
				break;
			case NETHER_ENTRY:
				netherEntry();
				break;
			case PRE_MINE_INIT:
				preMineInit();
				break;
			case PRE_MINE:
				preMine();
				break;
			case MINE:
				mine(isSafeToCancel);
				break;

		}

		return pathingCommand;
	}

	private void netherExit(boolean isSafeToCancel)
	{
		int dim = ctx.player().dimension;
		//logDirect(new Integer(dim).toString());
		boolean overworld = (dim == 0);
		//logDirect(String.valueOf(overworld));

		if(overworld && ctx.world().isBlockLoaded(ctx.playerFeet().down()))
		{
			objective = Objective.PRE_MINE_INIT;
		}
		pathingCommand = new PathingCommand(new GoalBlock(netherExitPoint), PathingCommandType.SET_GOAL_AND_PATH);

	}
	private void portalObsidianScan()
	{
		portalFrameObsidian.clear();
		for(BlockPos pos: obsidianLocations)
		{
			if (mineZone.isInsideMineZone(pos))
			{
				portalFrameObsidian.add(pos);
			}
		}

	}

	private void netherEntry()
	{
		updateObsidianLocation();


		if (!portalFrameObsidian.isEmpty())
		{
			objective = Objective.MINE;
		}
		else if(ctx.player().dimension == -1)
		{
				logDirect("Nether Entry");
				objective = Objective.NETHER_EXIT;
		}
		else
		{
			pathingCommand = new PathingCommand(new GoalBlock(netherEntryPoint), PathingCommandType.SET_GOAL_AND_PATH);
		}
	}

		private void preMineInit()
		{
			logDirect("init premine");
			logDirect(ctx.player().getPosition().toString());
			objective = Objective.PRE_MINE;
		}

		private void preMine()
		{
			IBlockState blockAtFeet = ctx.world().getBlockState(ctx.playerFeet());
			if(! ctx.world().isBlockLoaded(ctx.playerFeet(), false))
			{
				logDirect("nullified");
				pathingCommand = new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
				return;
			}


			EnumFacing.Axis playerAxis = EnumFacing.Axis.X;
			try
			{
				playerAxis = (EnumFacing.Axis) blockAtFeet.getProperties().get(
						blockAtFeet.getPropertyKeys().toArray()[0]);
			}
			catch(Exception e)
			{
				logDirect("Fuck");
			}

			if(playerAxis != null)
			{
				//logDirect(direct.toString());
			}
			else
			{
				logDirect("wtf");
				return;
			}
			if(ctx.world().getBlockState(ctx.playerFeet()).getBlock().equals(Blocks.PORTAL))
			{
				/*
				   switch(direct)
				   {
				   case Z : //HEY CHARLIE!!!
				   baritone.getLookBehavior().updateTarget(
				   RotationUtils.calcRotationFromCoords(ctx.playerFeet(), 
				   ctx.playerFeet().add(0,0,1)), true); //Cheap way of getting rotation
				//in the Z axis;

				case X : //HEY CHARLIE!!!
				baritone.getLookBehavior().updateTarget(
				RotationUtils.calcRotationFromCoords(ctx.playerFeet(), 
				ctx.playerFeet().add(1,0,0)), true);

				   }
				   logDirect("Fucking forward");

				   baritone.getInputOverrideHandler().setInputForceState(Input.MOVE_LEFT, true);
				   logDirect(new Boolean(baritone.getInputOverrideHandler().isInputForcedDown(Input.MOVE_LEFT)).toString());
				   pathingCommand = new PathingCommand(new GoalBlock(ctx.playerFeet().add(1,0,1)), PathingCommandType.SET_GOAL_AND_PATH);


				   return;
				   */
				preMinePos = ctx.playerFeet().add(1,0,1);
			}
			//else
			{

				if(!ctx.playerFeet().equals(preMinePos))
				{

					baritone.getInputOverrideHandler().setInputForceState(Input.MOVE_LEFT, true);
					logDirect(new Boolean(baritone.getInputOverrideHandler().isInputForcedDown(Input.MOVE_LEFT)).toString());
					pathingCommand = new PathingCommand(new GoalBlock(preMinePos), PathingCommandType.SET_GOAL_AND_PATH);
					return;
				}

				preMinePos = ctx.playerFeet();

				mineFieldCorner1 = preMinePos.add(new BlockPos(MINE_FIELD_RADIUS, MINE_FIELD_RADIUS, MINE_FIELD_RADIUS));
				//I'm a dirty bitch.
				mineFieldCorner2 = preMinePos.subtract(new BlockPos(MINE_FIELD_RADIUS, MINE_FIELD_RADIUS, MINE_FIELD_RADIUS));
				logDirect(mineFieldCorner1.toString());
				logDirect(mineFieldCorner2.toString());
				baritone.getInputOverrideHandler().clearAllKeys();
				objective = Objective.MINE;
			}



			//pathingCommand = new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);


		}

		private void printIfPortalBlock()
		{
			IBlockState blockAtFeet = ctx.world().getBlockState(ctx.playerFeet());
			if(blockAtFeet.getBlock() instanceof BlockPortal)
			{
				logDirect(blockAtFeet.getPropertyKeys().toString());
				ArrayList<EnumFacing.Axis> facing = new ArrayList<>();



				EnumFacing.Axis direct =(EnumFacing.Axis) blockAtFeet.getProperties().get(
						blockAtFeet.getPropertyKeys().toArray()[0]);
				if(direct != null)
				{
					logDirect(direct.toString());
				}



			}
		}


		private void updateObsidianLocation()
		{

			ArrayList<Block> scan = new ArrayList<>();
			scan.add(Blocks.OBSIDIAN);

			Baritone.getExecutor().execute(()->obsidianLocations = WorldScanner.INSTANCE.scanChunkRadius(ctx, scan, 256, -1, 6));
		}
		private void mine(boolean isSafeToCancel)
		{
			//logDirect("mine");

			//if(Baritone.settings().mineGoalUpdateInterval.value != 0  && tickCount++ % Baritone.settings().mineGoalUpdateInterval.value == 0)
			{
				portalFrameObsidian.clear();
				//logDirect("Scanning");

				updateObsidianLocation();

			}
			if(obsidianLocations == null)
			{
				return;
			}
			for(BlockPos pos: obsidianLocations)
			{
				if (inBetweenInclusive(mineFieldCorner1, mineFieldCorner2, pos))
				{
					portalFrameObsidian.add(pos);
				}
			}

			baritone.getInputOverrideHandler().clearAllKeys();
			List<BlockPos> allDroppedObsidian = droppedItemsScan();
			List<BlockPos> droppedObsidian = new ArrayList<>();
			for(BlockPos pos : allDroppedObsidian)
			{

				if(inBetweenInclusive(mineFieldCorner1, mineFieldCorner2, pos))
				{
					droppedObsidian.add(pos);
				}

			}
			List<BlockPos> placesToGo = new ArrayList<>();
			List<Goal> goalz = new ArrayList<>();

			goalz = new ArrayList<>();
			if(droppedObsidian.isEmpty() && portalFrameObsidian.isEmpty())
			{
				objective = Objective.NETHER_ENTRY;
				logDirect("NO OBSIDIAN FUCK");
				return;
			}
			for(BlockPos pos : portalFrameObsidian)
			{
				Optional<Rotation> rot = RotationUtils.reachable(ctx, pos);
				if(rot.isPresent() && isSafeToCancel)
				{
					baritone.getLookBehavior().updateTarget(rot.get(), true);
				}

				if(ctx.isLookingAt(pos))
				{
					MovementHelper.switchToBestToolFor(ctx,ctx.world().getBlockState(pos));
					baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
					pathingCommand = new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
					return;
				}
				else
				{

					{
						placesToGo.addAll(portalFrameObsidian);
						placesToGo.addAll(droppedObsidian);
						for(BlockPos position: placesToGo)
						{
							goalz.add(new GoalBlock(position));
						}
						pathingCommand = new PathingCommand(new GoalComposite(goalz.toArray(new Goal[0])), PathingCommandType.SET_GOAL_AND_PATH);
						return;

					}

				}

			}

		}
		public List<BlockPos> droppedItemsScan() {
			if (!Baritone.settings().mineScanDroppedItems.value) {
				return Collections.emptyList();
			}
			List<BlockPos> ret = new ArrayList<>();
			for (Entity entity : ctx.world().loadedEntityList) {
				if (entity instanceof EntityItem) {
					EntityItem ei = (EntityItem) entity;
					if (filter.has(ei.getItem())) {
						ret.add(new BlockPos(entity));
					}
				}
			}
			//ret.addAll(anticipatedDrops.keySet());
			return ret;
		}
		@Override
		public String displayName0()
		{
			return "I'm looping some fucking nether portalz!!!";
		}

		@Override
		public void onLostControl()
		{
			active = false;
		}
	}
