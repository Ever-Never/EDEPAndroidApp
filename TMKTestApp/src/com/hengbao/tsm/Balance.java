package com.hengbao.tsm;

import java.io.UnsupportedEncodingException;


public class Balance {

	  
	public Balance(){
		byte[] msg ;
		msg = new byte[]{(byte)0x00,(byte)0xA4,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x3F,(byte)0x00};	
		CommandList.addList(0, msg);
		msg = new byte[]{(byte)0x00,(byte)0xA4,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0xDF,(byte)0x04};	
		CommandList.addList(1, msg);
		msg = new byte[]{(byte)0x80,(byte)0x5c,0x00,0x02,0x04};	
		CommandList.addList(2, msg);
	}
	
	
	/** 
     * getbalance  ����ʽת������ʾ������9F7906 000012345678 9000 ���У�000012345678 ��ʾ��123456.78
     * @param magdate �����������ڼ��� ,ΪASSICֵ
     * @param offest  ������ֽ��ڼ����е�ƫ��λ�� 
     */  
	public int getbalance(byte[] appletaid){       
   		//GetSimInfo getSimInfo = new GetSimInfo();
		Result result =	GetSimInfo.DealAPDUs(appletaid);
		System.out.println("ָ���ѷ�API=GetSimInfo.DealAPDUs()");
		
		GetSimInfo.close();
		System.out.println("�ر�SimIfo��");
		System.out.println(result.getResValue());
		if(result.getResulttag())	
		{
			//int balance = Integer.valueOf(result.getResValue(),16);
			int balance = Integer.parseInt(result.getResValue(),16);
			return 	balance	;
		}
		System.out.print(result.getResulttag());
		return -1;
    } 
	
	/** 
     * showbalance  ����ʽת������ʾ������9F7906 000012345678 9000 ���У�000012345678 ��ʾ��123456.78
     * @param magdate �����������ڼ��� ,ΪASSICֵ
     * @param offest  ������ֽ��ڼ����е�ƫ��λ�� 
     */  
/*	public void showbalance(byte[] appletaid){       
		Result result =	GetSimInfo.DealAPDUs(appletaid);
		if(result.getResulttag())	
		{
			byte[] temp = new byte[6];
			System.arraycopy(result.getTagvalue(),0, temp, 0, 6);
			String balance = BytetoMoney(temp);
			int balance = Integer.valueOf(result.getResValue(),16);
			ShowDialog.creatDialog(0, "�˻����", "����ǰ���˻����Ϊ�� "+ balance +"Ԫ",null);
		}else{
			Util.bytesToString(result.getTagvalue()).replace(" ", "");
			ShowDialog.creatDialog(0, "��ʾ", "�˻�����ѯʧ�ܣ����Ժ����ԣ�",null);
		}
    } */
	public void showbalance(byte[] appletaid){    
		int balance =  getbalance(appletaid);
		System.out.println("ȡ������balance()����ֵ");
		System.out.print(balance);
		if(balance < 0)
		{
			ShowDialog.creatDialog(0, "��ʾ", "�˻�����ѯʧ�ܣ����Ժ����ԣ�",null);
		}else
		{
			ShowDialog.creatDialog(0, "�˻����", "����ǰ���˻����Ϊ�� "+ (balance/100.0) +"Ԫ",null);
		}
    } 
	
	
    /**����
   	* ��һ��6 byte��DCDֵתΪString�ͣ�����000012345678  ת��Ϊ123456.78
  ����* @param byte[] value   �洢ASSIC��ʽ������
  ����* @return һ���µ�Stringֵ
    */
    public String BytetoMoney(byte[] bytevalue)
    {
		String money = null;

		byte[] value = Util.ByteToAscii(bytevalue,bytevalue.length);
		try { 
			String result = new String(value,0,value.length,"utf-8");
			char[] temp = result.toCharArray();
			String lastdate  = String.valueOf(temp, temp.length-2,2);
			int i = 0;
			do{
				if(temp[i]!= '0')
				{
					break;
				}else{
					i += 1;
				}
			}while(i < temp.length-3);
			String frontdata = String.copyValueOf(temp, i, temp.length-i-2);
			money = frontdata.concat(".").concat(lastdate);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return money;
    }
    
}


