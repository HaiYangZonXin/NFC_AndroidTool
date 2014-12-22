package com.example.nfctagndef;

public class DataTypeTrans {

	public static String bytesToString(byte [] bytes){
        StringBuilder stringBuilder =new StringBuilder("\n");
        if (bytes==null || bytes.length <=0)
        {return null;}
        char[] buffer =new char[2];
        for (int i=0;i<bytes.length;i++)
        {
        	buffer[0] = Character.forDigit((bytes[i] >>> 4) & 0x0F, 16);
        	buffer[1] = Character.forDigit(bytes[i] & 0x0F, 16);
        	System.out.print(buffer);
        	stringBuilder.append(buffer);
        }
		return stringBuilder.toString() ;
	}
	public  byte intToByte(int byteData)
	{
		int intBuffer,i;
		byte byteResult=0;
		if (byteData <= 127)
		{	
			
			byteResult = (byte)byteData;              
		}
		else if (byteData ==128 )
		{
			byteResult = - 0;
		}
			else
			if(byteData <= 255 )
			{
				intBuffer =  byteData - 128;
				byteResult = (byte) - (intBuffer); 
			}
		
		return byteResult;
	}

}
