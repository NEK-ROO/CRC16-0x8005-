package CRC;

import java.math.BigInteger;
import java.util.Scanner;


/*
 * @overview:按提示输入所需数据，最终得到检验结果
 * @input:二进制形式的整数，如要发送整数5，则输入101
 * @output:1.输入提示
 * 	       2.验证结果(true or false)
 * @author:张羽白
 * @warning:1.输入二进制数不要超过16位
 * 			2.要求bit转换位置不要超过32
 */
public class CRC16 {
	private static int Poly;  //生成多项式16位（使用CRC16，简记码0x8005）
							  //(实际位数为16位,这里使用int是要模拟无符号位的short整形)
	private static int Send;  //发送方输入的原数据 16位
							  //(实际位数为16位,这里使用int是要模拟无符号位的short整形)
	
	private static int CRCr; //生成校验码时的余数(最后一次运算余数为校验码)
							 //(实际位数为16位,这里使用int是要模拟无符号位的short整形)
	
	private static long CRC;	//带校验码的数据 32位(由Send数据16位，校验码16位组成)
								//(实际位数为32位,使用long模拟无符号位int整形)
	
	private static long Rev; //接收到的带验证码的数据 32位
							 //(实际位数为32位,使用long模拟无符号位int整形)
	
	
	public static void main(String arg[]){
		Scanner sc = new Scanner(System.in);
		//提示输入二进制形式整数
		System.out.println("Please input the num you want to send");
		String SendStr = sc.next();
		//初始化Send和Poly
		Send = new BigInteger(SendStr, 2).intValue(); 
		Poly = 0x8005;
		//CRCr为余数，初始值等于Send
		CRCr = Send;
		//getCRC()初始化CRC值，得到32位(由Send数据16位，校验码16位组成)
		getCRC();
		//初始化接收方数据(初始化与CRC相等)
		Rev = CRC;
		getInfo();
		//提示输入是否要反转bit模拟数据出错情况(输入限制0-32，其中0为正确传输，不改变数据)
		System.out.println("Do you need to invert a bit?" + "\n" +
				"If no input 0,Else input the position number" + "\n" + 
				"(Number start from 1 and Position is from right to left)");
		int Position = sc.nextInt();
		
		if(Position == 0){
			getInfo();
			System.out.println("Result :" + verify());
		}
		else{
			invert(Position);
			getInfo();
			System.out.println("Result :" + verify());
		}
				
	}
	
	/*
	 * @intent:反转特定位置bit值
	 * @param:反转bit的位置(顺序为右到左)
	 * @return:无
	 */
	private static void invert(int position) {
		StringBuilder sb = new StringBuilder();
		for(int i = 1;i <= 32; i++)
			if(position == 32 - i + 1)
				sb.append("1");
			else
				sb.append("0");
		int xor = new BigInteger(sb.toString(), 2).intValue();  
		//修改Rev值,得到bit反转后的32位损坏数据
		Rev = Rev ^ xor;
	}
	
	/*
	 * @intent:检验数据是否正确
	 * @param:反转bit位置(顺序为右到左)
	 * @return:true for right
	 * 		   false for bad
	 */
	private static Boolean verify() {
		for(int i = 0; i < 32; i++){
			//最高位32位是否为1,若为1则做位异或(位除法),否则左移一位至最高位为1
			if((Rev & 0x80000000) != 0 /*&& Rev < */){
				Rev = (long) (Rev << 1);
				//poly左移16位对Rev高16位做位异或(位除法)
				Rev = Rev ^ ((long)Poly << 16);
				
			}
			else
				Rev = (long) (Rev << 1);
			//使Rev高32位为0
			Rev = Rev & 0xffffffffl;
		}
		if(Rev == 0)
			return true;
		else
			return false;
		
	}
	
	/*
	 * @intent:得到32位CRC码
	 * @param:无
	 * @return:无
	 */
	private static void getCRC() {
		//Send的16位移至高16位,低位补0
		CRC = (long)Send << 16;
		
		for(int i = 0; i < 16; i++){
			//if判断CRCr中最高位(16位)是否为1,是则做除法,否则左移一位直到最高位为1
			if((CRCr & 0x8000) != 0){
				CRCr = (int) (CRCr << 1);
				//位异或运算得到位除法余数CRCr(CRCr为最后一次余数时即为校验码)
				CRCr = (int) (CRCr ^ Poly);
			}
			else
				CRCr = (int) (CRCr << 1);
			//使CRCr高16位为0
			CRCr = CRCr & 0x0000ffff;
		}
		//将校验码放到CRC的低16位(CRC原来低16位由补0产生)
		CRC = CRC ^ CRCr;
	}
	
	/*
	 * @intent:输出重要数据
	 * @param:无
	 * @return:无
	 */
	private static void getInfo() {
		System.out.println("Send :" + Integer.toBinaryString(Send));
		System.out.println("CRCr :" + Integer.toBinaryString(CRCr));
		System.out.println("CRC :" + Long.toBinaryString(CRC));
		System.out.println("Rev :" + Long.toBinaryString(Rev));
		System.out.println("\n");
	}
}
