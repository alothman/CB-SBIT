package similar;

import java.io.FileNotFoundException;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class SBIT {

	public static void main(String [] argv) throws FileNotFoundException {
		try {
			// the source dataset filenames
			String[] sourceDatasets = {"TBot.arff","Zeus.arff","osx_trojan.arff","RBot.arff"};
			// the target dataset filename
			String targetDatasetName = "Sogou.arff";
			//load the target dataset
			DataSource target = new DataSource("botnet-data/"+targetDatasetName);
			Instances targetData = target.getDataSet();				
			targetData.setClassIndex(targetData.numAttributes() - 1);

			//create a randomforest model using the target data
			RandomForest rf1 = new RandomForest();
			rf1.buildClassifier(targetData);

			//test dataset
			String testData = "ISCX_Testing_new_Sogou_TEST.arff";
			DataSource test = new DataSource("botnet-data/"+testData);
			Instances testDataset = test.getDataSet();
			testDataset.setClassIndex(testDataset.numAttributes() - 1);

			//create an empty dataset to copy instances to it temporarily
			//later we concatenate this with the target dataset
			Instances dataToTransfer = new Instances(testDataset,0,0);

			//loop through source datasets
			int n = sourceDatasets.length;
			for(int i = 0; i < n; i++){
				//load the ith source dataset
				DataSource source1 = new DataSource("botnet-data/"+sourceDatasets[i]);
				Instances sourceData1 = source1.getDataSet();				
				sourceData1.setClassIndex(sourceData1.numAttributes() - 1);
				//loop through instances of ith source dataset
				for(int j = 0; j < sourceData1.numInstances(); j++){
					//get Instance object of current instance
					Instance srcInst = sourceData1.instance(j);
					//get attr values in a double array
					double[] srcAttrs = new double[srcInst.numAttributes()-1];
					for(int att = 0; att < (srcInst.numAttributes()-1); att++)
						srcAttrs[att] = srcInst.value(att);
					//loop through instances of target dataset
					for(int k = 0; k < targetData.numInstances(); k++){
						//get Instance object of current instance
						Instance trgInst = targetData.instance(k);
						//get attr values in a double array
						double[] trgAttrs = new double[trgInst.numAttributes()-1];
						for(int att = 0; att < (trgInst.numAttributes()-1); att++)
							trgAttrs[att] = trgInst.value(att);

						//now we have attr values for both src and trg instances
						//we can compute similarity between them
						double taniSim       = Distance.tanimotoSimiarity(srcAttrs, trgAttrs);
						double ellenbergSim  = Distance.ellenbergSimilarity(srcAttrs, trgAttrs);
						double gleasonSim    = Distance.gleasonSimilarity(srcAttrs, trgAttrs);
						double ruzickaSim    = Distance.ruzickaSimilarity(srcAttrs, trgAttrs);
						double brayCurtisSim    = Distance.brayCurtisSimilarity(srcAttrs, trgAttrs);
						//add current source instance of it passes the similarity thresholds
						if(taniSim > 0.55 && 
								ellenbergSim > 0.55 &&
								gleasonSim > 0.55 && 
								ruzickaSim > 0.55 && brayCurtisSim > 0.55
								){
							dataToTransfer.add(srcInst);
						}
					}
				}
			}


			// here we add the instances we have selected
			Instances newTargetData = new Instances(dataToTransfer);
			newTargetData.addAll(targetData);

			//create a randomforest model using the NEW target data
			//i.e. data after selecting instances from source datasets
			RandomForest rf2 = new RandomForest();
			rf2.buildClassifier(newTargetData);

			//keep track of class values for actuals and predicted
			String[] actuals = new String[testDataset.numInstances()];
			String[] rf1Predicted = new String[testDataset.numInstances()];
			String[] rf2Predicted = new String[testDataset.numInstances()];

			//Now loop through instances of test data and get
			//predictions for both RF models
			for (int i = 0; i < testDataset.numInstances(); i++) {
				//get Instance object of current instance
				Instance newInst = testDataset.instance(i);
				double actual = newInst.classValue();
				String actualClass = targetData.classAttribute().value((int) actual);
				actuals[i] = actualClass;

				//call classifyInstance, which returns a double value for the class
				//classify using model created using original target dataset
				double predicted = rf1.classifyInstance(newInst);
				String rf1PredictedClass = targetData.classAttribute().value((int) predicted);
				rf1Predicted[i] = rf1PredictedClass;
				//classify using model created using original target dataset
				predicted = rf2.classifyInstance(newInst);
				String rf2PredictedClass = targetData.classAttribute().value((int) predicted);
				rf2Predicted[i] = rf2PredictedClass;
			}

			System.out.println("=======================");
			double rf1Accuracy = compareResults(actuals, rf1Predicted);
			System.out.println("RF1 Accuracy:	" + rf1Accuracy );
			double rf2Accuracy = compareResults(actuals, rf2Predicted);
			System.out.println("RF2 (SBIT) Accuracy:	" + rf2Accuracy );

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** a small function to compute accuracy
	 * 
	 * @param actual the actual values
	 * @param predicted the predicted values
	 * @return accuracy 
	 */
	public static double compareResults(String actual[], String predicted[]){
		double equals = 0;
		//int unequals = 0;
		for(int i = 0; i < actual.length; i++){
			if(actual[i].equals(predicted[i])){
				equals++;
			}

		}
		return ((equals/actual.length)*100);
	}
}