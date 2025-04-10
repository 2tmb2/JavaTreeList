# Java  TreeList
This TreeList class is an AVL Tree-based implementation of a height-balanced Binary Tree. Insertion of duplicates are supported, as is index access for retreival, but deletion and insertion are based on the natural ordering of the elements. 

This class extends AbstractCollection, meaning it implements a subset of the Java Collections API. The methods it implements are as follows:
* boolean add(E e)
* boolean addAll(Collection<? extends E> c)
* void clear()
* boolean contains(Object o)
* boolean containsAll(Collection<?> c)
* boolean isEmpty()
* iterator<E> iterator()
* boolean remove(Object o)
* int size()
* Object[] toArray()
* <T> T[] toArray(T[] a)
* String toString()

Some important notes about this implementation:
* Concurrency modifications are not supported, meaning additions and removals partway through an iterator result in undefined behavior.
* Insertion and removal by index are not supported, as it does not fully implement the Java List Interface.
* For both Deletion and Insertion, the TreeList is unstable; for equivalent elements, the relative insertion order is *not* maintained.

This implementation was succcessful for all tests I attempted, however I cannot guarantee that it will work in all situations. Use at your own risk.
