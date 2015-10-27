package Gump.DataMing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataComputation {
	
	Map<String, Map<String, Double>> fileWordInfo = new HashMap<String, Map<String, Double>>(); //<filename, <word, totalNum>>
	Map<String, Map<String, Double>> TF_Map = new HashMap<String, Map<String, Double>>();//<word, Frequency>
	Map<String, Map<String, Double>> TfIdf_Map = new HashMap<String, Map<String, Double>>(); //<filename, <word, TF/IDF>>
	List<String> AllFilePath = new ArrayList<String>();//fileList
	//List<Float> CutWordSpeed = new ArrayList<Float>();
	double total = 0;

	/**
	 * 根据输入文档对数据进行初始化为Map
	 * @param AllFilePath
	 * @throws Exception
	 */
	public void initDataMap(List<String> AllFilePath) throws Exception{
		TextManipulation cutWord = new TextManipulation();
		for(String filePath:AllFilePath){
			List<String> resultList = cutWord.WordCut(filePath, true);
			Map<String, Double> wordInfo = new HashMap<String, Double>();//<word, totalNum>
			for(String word:resultList){
				if(!wordInfo.containsKey(word)){
					wordInfo.put(word, 1.0);
				}	else {
					double count = 0;
					count = wordInfo.get(word);
					wordInfo.put(word, count+1);
				}
			}
			fileWordInfo.put(filePath, wordInfo);
		}
	}
	
	/**
	 * 设置当前文档的全部文件路径
	 * @param dirPath
	 */
	public void setAllFilePath(List<String> dirPath){
		this.AllFilePath = dirPath;
	}
	
	/**
	 * 计算出每个词的词频TF，并放到Map中
	 */
	public void setTFMap(){
		for(String file:fileWordInfo.keySet()){
			Map<String, Double> wordCount = fileWordInfo.get(file);
			Map<String, Double> wordTF = new HashMap<String, Double>();
			double count = 0;
			for(String word:wordCount.keySet()){
				count += wordCount.get(word);
			}
			for(String word:wordCount.keySet()){
				wordTF.put(word, wordCount.get(word)/count);
			}
			TF_Map.put(file, wordTF); 
			total += count;
		}
	}
	
	/**
	 * 计算IDF
	 * @param totalNum
	 * @param existNum
	 * @return
	 */
	private double getIDF(int totalNum, double existNum){
		if(existNum == 0){
			existNum = 1;
		}
		return Math.log((1+totalNum)/existNum);
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<String, Double>> getTF_IDF() throws Exception{
		initDataMap(AllFilePath);
		setTFMap();
		for(String file:TF_Map.keySet()){
			Map<String, Double> wordMap = TF_Map.get(file);
			Map<String, Double> temp = new HashMap<String, Double>();
			for(String word:wordMap.keySet()){
				Double count = 0.0;
				for(String compareFile:TF_Map.keySet()){
					if(file.equals(compareFile)){
						continue;
					} else {
						Map<String, Double> compareWord = TF_Map.get(compareFile);
						if(compareWord.containsKey(word)){
							count += 1;
						}
					}
				}
				temp.put(word, wordMap.get(word)*getIDF(AllFilePath.size(), count));
			}
			TfIdf_Map.put(file, temp);
		}
		return TfIdf_Map;
	}
	
	public Double getWordCount(){
		return this.total;
	}
}