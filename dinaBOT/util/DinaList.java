package dinaBOT.util;

/**
 * DinaList is our simplistic implementation of ArrayList. Although it is not a formal ArrayList by java standards, it suits our purpose given that the "real" ArrayList from lejos currently doesn't work because of a linker error.
 *
*/
public class DinaList<E> {

	/* -- Class Variables -- */

	public static int default_capacity = 3;

	/* -- Instance Variables -- */

	Object[] element_data; //Elements of ArrayList
	int size; //Current size of the ArrayList

	/**
	 * Creates a new DinaList which is empty and has a initial capacity of 3 which will expand by a factor of two every time it becomes full.
	 *
	*/
	public DinaList() {
		size = 0; //Inital size 0
		element_data = new Object[3];
	}

	/**
	 * Adds an element to the DinaList. if the DinaList is full it will expand by a factor of 2.
	 *
	 * @param element the element to add to the DinaList
	*/
	public void add(E element) {
		if(size == element_data.length) {
			Object[] tmp_data = new Object[element_data.length*2];
			System.arraycopy(element_data, 0, tmp_data, 0, element_data.length);
			element_data = tmp_data;
		}
		element_data[size] = element;
		size++;
	}

	/**
	 * Searches for an element in the DinaList and removes it if found. Returns a boolean to indicate success or failure.
	 *
	 * @param element the element to remove from the DinaList
	 * @return true if and only if the element was successfully found and removed from the DinaList
	*/
	public boolean remove(E element) {
		for(int i = 0;i < size;i++) {
			if(element_data[i] == element) if(remove(i) != null) return true;
		}
		return false;
	}

	/**
	 * Removes an element from the DinaList according to it's index in the DinaList. If the element at index is null or if the index is invalid the method will return null. If the index is valid the element will be returned.
	 *
	 * @param index the index of the element to remove
	 * @return the removed element, null if the element at index is null or if the index is invalid
	*/
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		if(rangeCheck(index)) {
			E old_element = (E)element_data[index];

			int num_moved = size-index-1;
			if (num_moved > 0) System.arraycopy(element_data, index+1, element_data, index, num_moved);
			element_data[--size] = null; // Let gc do its work

			return old_element;
		}

		return null;
	}

	/**
	 * Returns an element from the DinaList according to it's index in the DinaList. If the element at index is null or if the index is invalid the method will return null. If the index is valid the element will be returned.
	 *
	 * @param index the index of the element to remove
	 * @return the element at index, null if the element at index is null or if the index is invalid
	*/
	@SuppressWarnings("unchecked")
	public E get(int index) {
		if(rangeCheck(index)) return (E)element_data[index];
		else return null;
	}

	/**
	 * Returns the current size of the DinaList. This is the number of elements in the DinaList right now
	 *
	*/
	public int size() {
		return size;
	}

	/**
	 * Returns if the DinaList is empty.
	 *
	 * @return true if and only if the DinaList is empty, false otherwise.
	*/
	public boolean isEmpty() {
		return (size == 0);
	}

	/**
	 * Checks if the range is valid
	 *
	 * @param index the index to check the validity of
	 * @return true if and only if the range is valid, false otherwise
	*/
	boolean rangeCheck(int index) {
		return (index >= 0 && index < size);
	}

}