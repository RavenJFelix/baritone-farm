package baritone.api.utils.math;

import net.minecraft.util.math.BlockPos;

/**
 * @author RavenJyroFelix
 * @since 02/05/2020
 */
public class MathHelper
{
	public static boolean inBetweenInclusive(int num1, int num2, int possible)
	{
		if((num1 <= possible && possible <= num2) || (num1 >= possible && possible >= num2))
		{
			return true;
		}
		else if (possible == num1 || possible == num2)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean inBetweenInclusive(BlockPos pos1, BlockPos pos2, BlockPos possible)
	{
		if(inBetweenInclusive(pos1.getX(), pos2.getX(), possible.getX())
				&& inBetweenInclusive(pos1.getY(), pos2.getY(), possible.getY())
				&& inBetweenInclusive(pos1.getZ(), pos2.getZ(), possible.getZ())
		  )
		{return true;} //Oh yes feel the dirt. feel so dirt Love the dirt.
		else {return false;} //Mmmmmmmm
	}


}
