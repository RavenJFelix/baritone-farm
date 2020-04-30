package baritone.process;

import baritone.api.process.INetherLoopProcess;

import baritone.utils.BaritoneProcessHelper;
import baritone.Baritone;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;

import net.minecraft.util.math.BlockPos;
public final class NetherLoopProcess extends BaritoneProcessHelper implements INetherLoopProcess
{


	private boolean active = false;
	private BlockPos netherEntryPoint;
	private BlockPos netherExitPoint;

	private enum objective
	{
		NETHER_ENTRY,
		NETHER_EXIT
	};

	@Override
	public void setNetherEntryPoint(BlockPos pos)
	{
		netherEntryPoint = pos;
	}

	public void setNetherExitPoint(BlockPos pos)
	{
		netherExitPoint = pos;
	}

	private objective dimensionalContex()
	{

		if(ctx.player().dimension == 0)
		{
			return objective.NETHER_ENTRY;
		}
		else
		{
			return objective.NETHER_EXIT;
		}
	}

	public NetherLoopProcess(Baritone baritone)
	{
		super(baritone);
	}

	@Override
	public boolean isActive() {return active;}

	@Override
	public void netherLoop()
	{
		active = true;
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

	}



	@Override 
	public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel)
	{

		GoalBlock goal;
		if(dimensionalContex() == objective.NETHER_ENTRY)
		{
			goal =new GoalBlock(netherEntryPoint);
		}
		else if(dimensionalContex() == objective.NETHER_EXIT)
		{
			goal = new GoalBlock(netherExitPoint);
		}
		else
		{
			goal = new GoalBlock(0,0,0);
		}

		return new PathingCommand(goal, PathingCommandType.SET_GOAL_AND_PATH);
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
