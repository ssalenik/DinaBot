package dinaBOT.mech;

/**
 * Description here
 *
 * @author Severin Smith, Gabriel Olteanu
 * @see Stacker
 * @version 1
*/
public interface Stacking {

	public boolean activateMechanicalClaw();

	public void openDockingBay();

	public void closeDockingBay();

	public boolean hold();

	public boolean release();

	public boolean tap();

	public boolean getDockStatus();

}