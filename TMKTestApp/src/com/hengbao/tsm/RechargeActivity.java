package com.hengbao.tsm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.*;
import java.text.*;

public class RechargeActivity extends Activity implements OnClickListener {

	Button rechargeBtn;
	TextView balanceT;
	EditText recmoneyE;
	TextView showpormptT;
	int curbalance;
	byte[] appletaid;
	final byte[] keyindex=new byte[]{(byte)0x01};//Ȧ����Կ������0x01
	final String strtradtype = "02";//�������ͣ���ֵ--0x02
	final byte[] terminalnum = new byte[]{(byte)0x12,(byte)0x34,(byte)0x56,(byte)0x78,(byte)0x90,(byte)0xAB};
	final String strterminalnum ="1234567890AB";
	final byte[] rechargekey = new byte[]{	(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
											(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
											(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
											(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF };//Ȧ����Կ
	
	MYDES mydes = new MYDES();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		ExitApplication.getInstance().addActivity(this);

		Bundle bundle = getIntent().getExtras();
		appletaid = bundle.getByteArray("aid");
/*		Intent intent = new Intent();
		appletaid = intent.getByteArrayExtra("aid");*/
		rechargeBtn = (Button) findViewById(R.id.recharge);
		balanceT = (TextView) findViewById(R.id.curbalance);
		recmoneyE = (EditText) findViewById(R.id.recMoney);
		showpormptT = (TextView) findViewById(R.id.pormpt);

		Balance balance = new Balance();
		curbalance = balance.getbalance(appletaid);
		if (curbalance < 0) {
			showpormptT.setText("�˻���ѯʧ�ܣ�");
			
		} else {
			balanceT.setText((curbalance/100.0) +"Ԫ");
			recmoneyE.setText("");
			rechargeBtn.setOnClickListener(this);// ��ֵ����
			rechargeBtn.setTag(1);
		}
	}

	private void rechargeDeal() {
		int totalbalance=0;
		int rechmoney=0;//inchmoney
		String moneyS = recmoneyE.getText().toString();
		if (moneyS == null || moneyS.equals("")) {
			ShowDialog.creatDialog(0, "��ʾ", "�������ֵ��", null);
			return;
		}
		rechmoney = Integer.parseInt(moneyS, 10)*100;//yuan to cent
		System.out.println(rechmoney);		
		if (rechmoney == 0) {
			ShowDialog.creatDialog(0, "��ʾ", "�������ֵ��", null);
			return;
		} else if(rechmoney > 100000){
			ShowDialog.creatDialog(0, "��ʾ", "�����ֵ������Χ�����������룡", null);
			return;
		}else
		{
			totalbalance = curbalance+rechmoney;
		}
		
		moneyS = Integer.toHexString(rechmoney);
		System.out.println(moneyS);
		byte[] moneyB = Util.HexString2Bytes(moneyS);//totalbalance > 65535 wrong	
		if (moneyB.length > 4) {
			ShowDialog.creatDialog(0, "��ʾ", "�˽��¿��ڽ�����Χ�����������룡", null);
			return;
		}
		System.out.println("load recharge commands!!");
		byte[] msg;
		msg = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00,(byte) 0x02, (byte) 0x3F, (byte) 0x00 };
		CommandList.addList(0, msg);
		msg = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00,(byte) 0x02, (byte) 0xDF, (byte) 0x04 };
		CommandList.addList(1, msg);
		
		msg = new byte[] { (byte) 0x80, (byte) 0x50,(byte)0x00,(byte)0x02, (byte)0x0b,(byte)0x01,
				(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x12,(byte)0x34,(byte)0x56,(byte)0x78,(byte)0x90,(byte)0xAB };
		System.arraycopy(keyindex, 0x00, msg, 5,keyindex.length);//keyindex
		System.arraycopy(moneyB, 0x00, msg,10-moneyB.length,moneyB.length);//recharge number
		System.arraycopy(terminalnum, 0x00, msg,10,terminalnum.length);//�ն˻����
		
		CommandList.addList(2, msg);
		msg = new byte[] {(byte)0x00,(byte)0xc0,(byte)0x00,(byte)0x00,(byte)0x10 };
		CommandList.addList(3, msg);
		System.out.println("load recharge commands over!!");
		Result result = GetSimInfo.DealAPDUs(appletaid);
		
		System.out.println("commands oK!!");
		System.out.println(result.getSW());
		System.out.println(result.getResValue());
		
		String res = result.getResValue();//0x10byte=���4+�����������2+��Կ�汾��1+�㷨��ʶ1+α�����4+MAC1(4byte)
		String strcurblance = res.substring(0, 8);
		System.out.println(strcurblance);
		String strtradnum = res.substring(8, 12);
		System.out.println(strtradnum);
		String strkeyverison = res.substring(12, 14);
		System.out.println(strkeyverison);
		String strclacflag = res.substring(14, 16);
		System.out.println(strclacflag);
		String strrandom = res.substring(16, 24);
		System.out.println(strrandom);
		String strmac1 = res.substring(24, 32);
		System.out.println(strmac1);
		
		String striv = strrandom + strtradnum+"8000"; //��ɢ����
		System.out.println("��ɢ���ӣ�"+striv);
		System.out.println("ԭʼkey��"+Util.bytesToHexString(rechargekey));
		byte[] byteiv = Util.String2Bytes(striv);//װ��Ϊbyte����
		byte[] sattkey = mydes.TDesEncrypt(rechargekey, byteiv);
		System.out.println("������Կ��"+Util.bytesToHexString(sattkey));
		
		String strrechmoney = Integer.toHexString(rechmoney);
		int cnt = 8 - strrechmoney.length();
		while((cnt--) > 0)
		{
			strrechmoney ="0"+strrechmoney;
		}
		
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
		String strdatetime = format.format(new Date(System.currentTimeMillis()));
		System.out.println("ʱ�䣺"+strdatetime);
		
		String strMAC2data = strrechmoney+strtradtype+strterminalnum+ strdatetime +"800000000000"; //mac2����Դ����
		byte[] byteMAC2data = Util.String2Bytes(strMAC2data);//װ��Ϊbyte����

		System.out.println("��ɢ��Կ��"+Util.bytesToHexString(sattkey));
		System.out.println("macԴ���ݣ�"+Util.bytesToHexString(byteMAC2data));
		byte[] macdata = mydes.getMAC(sattkey, byteMAC2data);
		System.out.println("mac��"+Util.bytesToHexString(macdata));
		
		byte[] apdu = new byte[]{(byte)0x80,(byte)0x52,(byte)0x00,(byte)0x00,(byte)0x0B,(byte)0x00,
				(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00 };
		byte[] bytedatatime = Util.String2Bytes(strdatetime);
		System.arraycopy(bytedatatime, 0, apdu, 5, 7);
		System.arraycopy(macdata, 0, apdu, 12, 4);
		System.out.println("mac��"+Util.bytesToHexString(apdu));
		result = GetSimInfo.DealAPDU(apdu);
		
		apdu = new byte[]{(byte)0x00,(byte)0xc0,(byte)0x00,(byte)0x00,(byte)0x04 };
		result = GetSimInfo.DealAPDU(apdu);
		if (!result.getResulttag()) {
			ShowDialog.creatDialog(0, "��ʾ", "��ֵʧ�ܣ�", null);
			return;
		}
		apdu = new byte[]{(byte)0x80,(byte)0x5c,0x00,0x02,0x04};
		result = GetSimInfo.DealAPDU(apdu);//get balance
		GetSimInfo.close();
		System.out.println("�ر�SimIfo��");
		System.out.println(result.getSW());
		System.out.println(result.getResValue());
		
		String show;
		if (result.getResulttag()) {
			curbalance = Integer.parseInt(result.getResValue(), 16);
			if (curbalance ==  totalbalance) {
				moneyS = ""+(curbalance/100.0)+"Ԫ";
				balanceT.setText(moneyS);
				recmoneyE.setText(""+(rechmoney/100));
				show = "��ֵ�ɹ������ڵ�ǰ���Ϊ��" + (curbalance/100.0) + "Ԫ��";
				showpormptT.setText(show);
			} else {
				show = "��ֵʧ�ܣ����ڵ�ǰ�����Ԥ�ڲ����ϣ�(ʵ�ʣ�"+curbalance +"��������"+ totalbalance+")";
				showpormptT.setText(show);
			}
		} else {
			show = "��ֵʧ�ܣ�";
			showpormptT.setText(show);
		}
		ShowDialog.creatDialog(0, "��ʾ", show, null);
		return;		
	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		switch (tag) {
		case 1:
			rechargeDeal();
			break;

		default:
			break;
		}
	}
/*	@Override
    protected void onDestroy() {
		ExitApplication.getInstance().exit();
	}*/
	public static byte uniteBytes(byte src0, byte src1) {
  	  byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
  	    .byteValue();
  	  _b0 = (byte) (_b0 << 4);
  	  byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
  	    .byteValue();
  	  byte ret = (byte) (_b0 ^ _b1);
  	  return ret;
  }
}
