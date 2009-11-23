package dinaBOT.mech;

/**
 * Description here
 *
 * @author Severin Smith, Gabriel Olteanu
 * @see Stacker
 * @version 1
*/
public interface Stacking {

	public boolean pickUp();

	public void openCage();

	public void closeCage();

	public boolean hold();

	public boolean release();

	public boolean tap();

	public boolean getCageStatus();

	public boolean close();

}