package com.hengbao.tsm;

import java.util.ArrayList;
import java.util.List;


//��֯Ҫ���͵�ָ���
public class CommandList {

	private static List<byte[]> mListcmd = new ArrayList<byte[]>();
	
/*	public CommandList()
	{
		mListcmd = new ArrayList<byte[]>();;
	}
*/	
	//����ָ��е�ָ��
	public static void addList(int location,byte[] msg)
	{
		mListcmd.add(location, msg);
	}
	
	//���ָ��е�ָ��
	public static void clearList()
	{
		if(!mListcmd.isEmpty())
		{
			mListcmd.clear();
		}
	}
	
	//��ȡָ��е�ָ��
	public static byte[] getList(int location)
	{
/*		for(String temp:mListcmd)
		{
			return temp;
		}
*/      
/*		Iterator it1 = mListcmd.iterator();
        while(it1.hasNext())
        {
        	return it1.;
        }*/
		return mListcmd.get(location);
	}
	
	//��ȡָ��е�ָ��
	public static int getListSize()
	{
		return mListcmd.size();
	}
}