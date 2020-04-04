package uk.digitalsquid.internetrestore.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Proxies all list operations to the given list
 * @author william
 *
 * @param <E>
 */
public class ListProxy<E> implements List<E> {
	
	private List<E> p;
	
	/**
	 * Note that if this constructor is used then 
	 */
	public ListProxy() {
	}
	
	public ListProxy(List<E> proxyTo) {
		setProxy(proxyTo);
	}
	
	public void setProxy(List<E> proxyTo) {
		p = proxyTo;
	}
	
	/*
	 * FROM HERE ON IS LIST INTERFACE METHODS.
	 */
	
	@Override
	public boolean add(E arg0) {
		return p.add(arg0);
	}

	@Override
	public void add(int arg0, E arg1) {
		p.add(arg0, arg1);
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		return p.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		return p.addAll(arg0, arg1);
	}

	@Override
	public void clear() {
		p.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return p.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return p.containsAll(arg0);
	}

	@Override
	public E get(int arg0) {
		return p.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return p.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return p.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return p.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return p.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<E> listIterator() {
		return p.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int arg0) {
		return p.listIterator(arg0);
	}

	@Override
	public E remove(int arg0) {
		return p.remove(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		return p.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return p.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return p.retainAll(arg0);
	}

	@Override
	public E set(int arg0, E arg1) {
		return p.set(arg0, arg1);
	}

	@Override
	public int size() {
		return p.size();
	}

	@Override
	public List<E> subList(int arg0, int arg1) {
		return p.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return p.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return p.toArray(arg0);
	}
}
