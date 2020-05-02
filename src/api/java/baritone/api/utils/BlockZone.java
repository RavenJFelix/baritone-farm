package baritone.api.utils;

/**
 * @author RavenJyroFelix
 * @since 02/05/2020
 */

import net.minecraft.util.math.BlockPos;

public class BlockZone
{
	private BlockPos corner1;
	private BlockPos corner2;

	BlockZone(BlockPos newCorner1, BlockPos newCorner2)
	{
		corner1 = newCorner1;
		corner2 = newCorner2;
	}

	public BlockPos getCorner1()
	{
		return corner1;
	}

	public BlockPos getCorner2()
	{
		return corner2;
	}

	public void setCorner1(BlockPos newCorner1)
	{
		corner1 = newCorner1;
	}

	public void setCorner2(BlockPos newCorner2)
	{
		corner2 = newCorner2;
	}

}   

