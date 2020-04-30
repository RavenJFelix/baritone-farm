package baritone.command.defaults;
import baritone.api.command.Command;
import baritone.api.IBaritone;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandException;
import baritone.api.cache.IWaypoint;
import baritone.api.cache.Waypoint;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.command.datatypes.BlockById;

import java.util.stream.Stream;
import java.util.List;
import java.util.Arrays;
public class NetherLoopCommand extends Command
{
	public NetherLoopCommand(IBaritone baritone) {
		super(baritone, "netherloop");
	}

	@Override
	public void execute(String label, IArgConsumer args) throws CommandException
	{
		IWaypoint[] waypoints = ForWaypoints.getWaypointsByTag(this.baritone, IWaypoint.Tag.USER);
		
		if(waypoints == null)
		{
			logDirect("Fuck there's no waypoints)");
		}
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

		baritone.getNetherLoopProcess().netherLoop();

	}
	@Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return args.tabCompleteDatatype(BlockById.INSTANCE);
    }

    @Override
    public String getShortDesc() {
        return "Mine some blocks";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "THIS LOOPS NETHER YEAH",
                "",
                "The specified blocks can be ores (which are commonly cached), or any other block.",
                "",
                "Also see the legitMine settings (see #set l legitMine).",
                "",
                "Usage:",
                "> mine diamond_ore - Mines all diamonds it can find.",
                "> mine redstone_ore lit_redstone_ore - Mines redstone ore.",
                "> mine log:0 - Mines only oak logs."
        );
    }
}
