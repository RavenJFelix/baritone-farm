package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandException;

import java.util.Arrays;
import java.util.List;

import java.util.stream.Stream;

public class FuckCommand extends Command
{
	public FuckCommand(IBaritone baritone)
	{
		super(baritone, "fuck");
	}
	@Override
	public void execute(String label, IArgConsumer args) throws CommandException
	{
		args.requireExactly(0);

		logDirect("Fuck fuck fuck fuck!");

	}
	
	@Override
	public String getShortDesc()
	{
		return "Say fuck in chat";
	}

	@Override 
	public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException
	{
		return null;
	}

	@Override
	public List<String> getLongDesc()
	{
		return Arrays.asList(
				"This ssays fuck in the chat",
				"",
				"That's all it fucking does",
				"The fucking end."
				);
	}

}

