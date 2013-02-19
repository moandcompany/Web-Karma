package edu.isi.karma.cleaning;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

public class Test {
	public static void test2()
	{
		Vector<Integer> poss = new Vector<Integer>();
		poss.add(1);
		poss.add(3);
		poss.add(4);
		ProgSynthesis ps = new ProgSynthesis();
		Vector<Vector<Integer>> p = new Vector<Vector<Integer>>();
		p = ps.generateCrossIndex(poss, p, 0);
		for(Vector<Integer> i:p)
		{
			System.out.println(""+i);
		}
	}
	//test cross merge
	public static void test3()
	{
		Vector<String[]> examples = new Vector<String[]>();
		String[] xStrings = {"<_START>http://dbpedia.org/resource/Air_Europa<_END>","Air Europa"};
		String[] yStrings ={"<_START>http://dbpedia.org/resource/European_Aviation_Air_Charter<_END>","European Aviation Air Charter"};
		String[] zStrings = {"<_START>http://dbpedia.org/resource/Grossmann_Jet_Service<_END>","Grossmann Jet Service"};
		String[] mStrings = {"<_START>http://dbpedia.org/resource/US_Airways<_END>","US Airways"};
		examples.add(xStrings);
		examples.add(yStrings);
		examples.add(zStrings);
		examples.add(mStrings);
		ProgSynthesis psProgSynthesis = new ProgSynthesis();
		psProgSynthesis.inite(examples);
		Collection<ProgramRule> p = psProgSynthesis.run_main();
		String value = "http://dbpedia.org/resource/Air_Malta";
		ProgramRule progString = p.iterator().next();
		InterpreterType worker = progString.getRuleForValue(value);
		String reString = worker.execute(value);
		System.out.println("===========Results===================");
		System.out.println(reString);
	}
	public static void test4(String dirpath)
	{
		File nf = new File(dirpath);
		File[] allfiles = nf.listFiles();
		//statistics
		DataCollection dCollection = new DataCollection();
		//list all the csv file under the dir
		for(File f:allfiles)
		{
			Vector<String[]> examples = new Vector<String[]>();
			Vector<String[]> entries = new Vector<String[]>();	
			try
			{
				if(f.getName().indexOf(".csv")==(f.getName().length()-4))
				{					
					CSVReader cr = new CSVReader(new FileReader(f), ',','"','\0');
					String[] pair;
					String corrResult = "";
					while ((pair=cr.readNext())!=null)
					{
						if(pair == null || pair.length <=1)
							break;
						entries.add(pair);
						corrResult += pair[1]+"\n";
					}
					if(entries.size() <=1)
						continue;
					String[] mt = {"<_START>"+entries.get(0)[0]+"<_END>",entries.get(0)[1]};
					examples.add(mt);
					while(true) // repeat as no correct answer appears.
					{
						ProgSynthesis psProgSynthesis = new ProgSynthesis();
						psProgSynthesis.inite(examples);
						Vector<ProgramRule> pls = new Vector<ProgramRule>();
						pls.addAll(psProgSynthesis.run_main());
						String[] wexam = null;
						if(pls.size()==0)
							break;
						long t1 = System.currentTimeMillis();
						for(int i = 0; i<pls.size(); i++)
						{		
							ProgramRule script = pls.get(i);
							//System.out.println(script);
							
							for(int j = 1; j<entries.size(); j++)
							{
								InterpreterType worker = script.getRuleForValue(entries.get(j)[0]);
								String s = worker.execute(entries.get(j)[0]);
								System.out.println("result:   "+s);
								if(s== null||s.length()==0)
								{
									wexam = entries.get(j);
									String p[] = {"<_START>"+wexam[0]+"<_END>",wexam[1]};
									wexam = p;
									s = entries.get(j)[0];
									break;
								}
								if(s.compareTo(entries.get(j)[1])!=0)
								{
									wexam = entries.get(j);
									String p[] = {"<_START>"+wexam[0]+"<_END>",wexam[1]};
									wexam = p;
									s = entries.get(j)[0];
									break;
								}						
							}
							if(wexam == null)
								break;
						}	
						long t2 = System.currentTimeMillis();
						FileStat fileStat = new FileStat(f.getName(), psProgSynthesis.learnspan, psProgSynthesis.genspan, (t2-t1), examples.size(), examples, psProgSynthesis.ruleNo);
						dCollection.addEntry(fileStat);
						if(wexam != null)
							examples.add(wexam);
						else {
							break;
						}
							
					}							
				}				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		dCollection.print();
	}
	
	//test the classifier
	public static void test6()
	{
		Vector<String[]> examples = new Vector<String[]>();
		String[] xStrings = {"<_START>Bulevar kralja Aleksandra&nbsp;156<_END>","Bulevar kralja Aleksandra*156"};
		String[] yStrings ={"<_START>Dositejeva&nbsp;22<_END>","Dositejeva*22"};
		String[] zStrings ={"<_START>Bobby's Restaurant,London<_END>","Bobby's Restaurant"};
		String[] pStrings ={"<_START>1 Lombard street,London<_END>","1 Lombard street"};
		String[] qStrings = {"<_START>5th ave,New York<_END>","5th ave"};
		String[] rStrings = {"<_START>2008-09-07<_END>","09/07/2008"};
		examples.add(xStrings);
		examples.add(yStrings);
		examples.add(rStrings);
		examples.add(zStrings);
		examples.add(pStrings);
		examples.add(qStrings);
		ProgSynthesis psProgSynthesis = new ProgSynthesis();
		psProgSynthesis.inite(examples);
		psProgSynthesis.run_main();
		//System.out.println(""+psProgSynthesis.classifier.test("2009-07-11"));
	}
	
	public static void test7()
	{
		Vector<String[]> examples = new Vector<String[]>();
		String[] xStrings ={"<_START>(323)-708-7700<_END>","323-708-7700"};
		String[] yStrings ={"<_START>(425)-706-7709<_END>","425-706-7709"};
		String[] zStrings ={"<_START>510.220.5586<_END>","510-220-5586"};
		String[] pStrings ={"<_START>323.710.7700<_END>","323-710-7700"};
		String[] qStrings ={"<_START>235 7654<_END>","425-235-7654"};
		String[] rStrings ={"<_START>745 8139<_END>","425-745-8139"};
		examples.add(xStrings);
		examples.add(yStrings);
		examples.add(zStrings);
		examples.add(pStrings);
		examples.add(qStrings);
		examples.add(rStrings);
		ProgSynthesis psProgSynthesis = new ProgSynthesis();
		psProgSynthesis.inite(examples);
		Collection<ProgramRule> p = psProgSynthesis.run_main();
		String value = "(323)-708-7800";
		String value1 = "508 7800";
		ProgramRule progString = p.iterator().next();
		InterpreterType worker = progString.getRuleForValue(value);
		System.out.println(""+progString.getClassForValue(value));
		String reString = worker.execute(value);
		InterpreterType worker1 = progString.getRuleForValue(value1);
		System.out.println(""+progString.getClassForValue(value1));
		String reString1 = worker1.execute(value1);
		System.out.println("/*===========Results===================*/");
		System.out.println(reString);
		System.out.println(reString1);
	}
	
	
	//test Sumit's approach
//	public static void test11()
//	{
//		Vector<String[]> examples = new Vector<String[]>();
//		String[] xStrings = {"<_START>a1b2c3d4e5#g6h<_END>","g6h,a1b2c3d4e5"};
//		String[] zStrings ={"<_START>m1n2r3s4t5#x6y<_END>","x6y,m1n2r3s4t5"};
//		//String[] yStrings ={"<_START>#p1q<_END>","p1q#c3d4e"};
//		examples.add(xStrings);
//		//examples.add(yStrings);
//		examples.add(zStrings);
//		long t1 = System.currentTimeMillis();
//		ProgSynthesis psProgSynthesis = new ProgSynthesis();
//		psProgSynthesis.inite(examples);
//		HashSet<String> p = psProgSynthesis.run_sumit();
//		long t2 = System.currentTimeMillis();
//		ProgSynthesis psProgSynthesis1 = new ProgSynthesis();
//		psProgSynthesis1.inite(examples);
//		HashSet<String> q = psProgSynthesis1.run_main();
//		long t3 = System.currentTimeMillis();
//		double timespan1 = (t2 -t1)*1.0/60000;
//		double timespan2 = (t3 -t2)*1.0/60000;
//		System.out.println("span 1:"+timespan1+"\nspan 2:"+timespan2);
//		
//	}
	public static void main(String[] args)
	{
		Test.test4("/Users/bowu/Research/testdata/TestSingleFile");
		//Test.test3();
	}
}
