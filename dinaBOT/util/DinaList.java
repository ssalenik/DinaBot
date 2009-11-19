package dinaBOT.util;

public class DinaList<E> {

	public static int default_capacity = 3;

	/* -- */

	Object[] element_data;
	int element_pointer;

	public DinaList() {
		element_data = new Object[3];

		element_pointer = 0;
	}

	public void add(E element) {
		if(element_pointer == element_data.length) {
			Object[] tmp_data = new Object[element_data.length*2];
			System.arraycopy(element_data, 0, tmp_data, 0, element_data.length);
			element_data = tmp_data;
		}

		element_data[element_pointer] = element;
		element_pointer++;
	}

	public boolean remove(E element) {
		for(int i = 0;i < element_pointer;i++) {
			if(element_data[i] == element) if(remove(i) != null) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public E remove(int index) {
	 if(rangeCheck(index)) {
	 E old_element = (E)element_data[index];

	 int num_moved = element_pointer - index;
	 if (num_moved > 0) System.arraycopy(element_data, index+1, element_data, index, num_moved);
	 element_data[--element_pointer] = null; // Let gc do its work

	 return old_element;
		}

 	return null;
	}

	@SuppressWarnings("unchecked")
 public E get(int index) {
		if(rangeCheck(index)) return (E)element_data[index];
		else return null;
	}

	public int size() {
		return element_pointer+1;
	}

	public boolean isEmpty() {
		return (element_pointer <= 0);
	}

	boolean rangeCheck(int index) {
		return (index >= 0 && index < element_pointer);
	}

}