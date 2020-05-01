package baritone.api.process;

import net.minecraft.util.math.BlockPos;
public interface INetherLoopProcess extends IBaritoneProcess
{
	void netherLoop();
	void setNetherEntryPoint(BlockPos playerPos);
	void setNetherExitPoint(BlockPos playerPos);
	void setPortalMinePoint(BlockPos pos);

}
