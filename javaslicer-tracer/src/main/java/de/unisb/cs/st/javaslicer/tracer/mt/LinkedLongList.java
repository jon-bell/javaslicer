package de.unisb.cs.st.javaslicer.tracer.mt;



public class LinkedLongList implements Cloneable {
	public static class Node implements Cloneable{
		public long entry;
		public Node next;
	}
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	private Node first;
	private Node last;
	public synchronized boolean addUnique(long o)
	{
		Node i = first.next;
		while(i != null)
		{
			if(i.entry == o)
				return false;
			i = i.next;
		}
		Node n = new Node();
		n.entry = o;
		last.next=n;
		last = n;
		return true;
	}
	
	public synchronized void add(long o)
	{
		Node n = new Node();
		n.entry = o;
		if(first.next == null)
		{
			first.next=n;
			last = n;
		}
		else
		{
			n.next = first.next;
			first.next = n;
		}
		
	}
	public Node getFirst()
	{
		return first.next;
	}
	public LinkedLongList()
	{
		clear();
	}
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("[");
		Node e = getFirst();
		while(e != null)
		{
			ret.append(e.entry);
			ret.append(",");
			e = e.next;
		}
		ret.append("]");
		return ret.toString();
	}
	public void clear() {
		first = new Node();
		last = first;
	}
}