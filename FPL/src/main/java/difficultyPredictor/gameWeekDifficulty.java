package difficultyPredictor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class gameWeekDifficulty {
	
	public static File file;
	public static FileInputStream fileStream;
	public static FileOutputStream writeFile;
	public static XSSFWorkbook workbook;
	public static XSSFSheet weightsSheet;
	public static XSSFSheet fixtureSheet;
	public static XSSFSheet resultSheet;
	
	public static Map<String,int[]> getWeightage() throws IOException {
		file = new File(System.getProperty("user.dir")+"/src/main/resources/Schedule.xlsx");
		fileStream = new FileInputStream(file);
		workbook = new XSSFWorkbook(fileStream);
		weightsSheet = workbook.getSheet("Team Weightage");
		Map<String,int[]> weights=new HashMap<String,int[]>();
		String key = null;
		for(int i=0;i<20;i++) {
			int[] values=new int[2];
			Row row = weightsSheet.getRow(i);
			for(int j=0;j<3;j++) {
				Cell cell=row.getCell(j);
				switch(cell.getCellType())
				{
				case STRING:
					key=cell.getStringCellValue();
					break;
				case NUMERIC:
					values[j-1]=(int) cell.getNumericCellValue();
					break;
				default:
					System.out.println("Error");
				}
			}
			weights.put(key, values);	
		}
		return weights;
	}
	
	public static void printMap(Map<String,int[]> map) {
		for (String keys : map.keySet())  
		{
			System.out.println("Club: " +keys);
		    int[] values=map.get(keys);
		    for(int val: values)
		    	System.out.println(val);
		}
	}
	
	public static Map<String,int[]> calculateDifficulty(Map<String,int[]> weights,int startWeek,int noOfWeeks) throws IOException {
		fixtureSheet = workbook.getSheet("Fixtures");
		Map<String,int[]> difficulty=new HashMap<String,int[]>();
		for(int i=1;i<=20;i++) {
			String club;
			int[] difficultyMatrix=new int[3];
			Row row=fixtureSheet.getRow(i);
			club=row.getCell(0).getStringCellValue();
			for(int j=startWeek;j<=(startWeek+noOfWeeks-1);j++) {
				String versusClub=row.getCell(j).getStringCellValue();
				int[] versusClubWeightage=weights.get(versusClub);
				difficultyMatrix[0]+=versusClubWeightage[0];
				difficultyMatrix[1]+=versusClubWeightage[1];
				difficultyMatrix[2]+=versusClubWeightage[0]+versusClubWeightage[1];
			}
			difficulty.put(club, difficultyMatrix);
		}
		return difficulty;
	}
	/*
	public static void sortClubsOnDifficulty(Map<String,int[]> difficulty) {
		List sortedClubs=new ArrayList();
		
		
	}
	*/
	public static void writeToSheet(Map<String,int[]> diff) throws IOException {
		resultSheet = workbook.getSheet("Analysis");
		int i=0;
		for(String keys:diff.keySet()) {
			Row row=resultSheet.createRow(i);
			row.createCell(0).setCellValue(keys);
			int j=1;
			for(int val:diff.get(keys)) {	
				row.createCell(j++).setCellValue(val);
			}
			i++;
		}
		writeFile = new FileOutputStream(file);
		workbook.write(writeFile);
		workbook.close();
		writeFile.close();
	}
	
	public static void main(String[] args) throws IOException {
		Map<String,int[]> weights=getWeightage();
		//printMap(weights);
		System.out.println("Enter the current gameweek and the number of weeks to calculate: ");
		Scanner in=new Scanner(System.in);
		int start=in.nextInt();
		int weeks=in.nextInt();
		Map<String,int[]> diff=calculateDifficulty(weights,start,weeks);
		printMap(diff);
		writeToSheet(diff);
	}
}
