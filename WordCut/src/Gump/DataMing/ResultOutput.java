package Gump.DataMing;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ResultOutput {

	private static String TestPath = "./ComparetData/InputFile.txt";
	private static String StardPath = "./ComparetData/StardFile.txt";
	private static String TextPath = "./TestData";
	private static Map<String, Map<String, Double>> DataMap= new HashMap<String, Map<String, Double>>();
	private static List<String> FileList = new ArrayList<String>();
	private static long startTime;
	private static long endTime;
	private static double count;
	private float rate;

	public static void main(String[] args) throws Exception{
		System.out.println("*************************************** 准确率测试 ****************************************");
		System.out.println("载入准确率测试文档：" + TestPath);
		System.out.println("载入人工分词文档：" + StardPath + "/n");
		ResultOutput output = new ResultOutput();
//		output.PrintAccuracyRate();
		System.out.println();

		try{
			Thread.sleep(2000);
		} catch (InterruptedException e){
		}

		System.out.println("*************************************分词测试与TF/IDF计算***********************************");
		System.out.println("正在进行分词与TF/IDF计算....");
		startTime=System.currentTimeMillis(); 
		output.getTfIdf();
		endTime=System.currentTimeMillis();
		double rate = count*1000/(endTime-startTime);

		System.out.println("分词与TF/IDF计算结束!");
		System.out.println("分词速度为: " + rate + "词/秒");
		System.out.println();

		System.out.println("*************************************结果查看***********************************");
		getFileList();
		System.out.println("计算已经结束，共处理文档 " + FileList.size() + " 个");
		Scanner scanner = new Scanner(System.in);
		while(true){
			System.out.print("请输入需要查看的文档序号（1-" + FileList.size() + "):");
			String Num = scanner.next();
			if(!Num.equals("exit")){
				Integer num = Integer.parseInt(Num);
				output.PrintResult(TextPath + "/" + FileList.get(num-1));
			} else {
				break;
			}
			System.out.println();;
		}
		scanner.close();
	}
	
	private static void getFileList(){
		File fileList = new File(TextPath);
		for(String file:fileList.list()){
			if(!file.equals(".DS_Store")){
				FileList.add(file);
			}
		}
	}
	
	
	private void PrintAccuracyRate() throws Exception{
		Comparation comparation = new Comparation();
		rate = comparation.getAccuracyRate(TestPath, StardPath);
		System.out.println("准确率测试结果:" + rate);
	}
	
	
	private void getTfIdf() throws Exception{
		File dirList = new File(TextPath);
		List<String> filePath = new ArrayList<String>();
		for(String dirName:dirList.list()){
			if(dirName.equals(".DS_Store")){
				continue;
			}
			filePath.add(TextPath+"/"+dirName);
		}
		DataComputation compute = new DataComputation();
		compute.setAllFilePath(filePath);
		DataMap = compute.getTF_IDF();
		count = compute.getWordCount();
	}
	
	private void PrintResult(String fileName){
		System.out.println();
		String[] textName = fileName.split("/");
		System.out.println("----------------------------------------" + "文档" + ":" + textName[(textName.length)-1]+ "-------------------------------------------");
		Map<String, Double> wordResult = DataMap.get(fileName);
		int wordNum = 0;
		for(String word:wordResult.keySet()){
			if((wordNum%5) == 0){
				System.out.print("\n");
			}
			DecimalFormat df = new DecimalFormat( "0.00000000 ");
			double value = wordResult.get(word);
			System.out.print(word + "=" + df.format(value) + "    ");
			wordNum++;
		}
		System.out.println();
		System.out.println();
	}
}