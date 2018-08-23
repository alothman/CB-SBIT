package similar;

//http://showme.physics.drexel.edu/usefulchem/Software/Drexel/Cheminformatics/Java/cdk/src/org/openscience/cdk/similarity/Tanimoto.java
public class Distance 
{



	/**
	 * Evaluates the continuous Tanimoto coefficient for two real valued vectors.
	 * From:
	 * http://showme.physics.drexel.edu/usefulchem/Software/Drexel/Cheminformatics/Java/cdk/src/org/openscience/cdk/similarity/Tanimoto.java
	 * @param features1 The first feature vector
	 * @param features2 The second feature vector
	 * @return The continuous Tanimoto coefficient
	 */
	public static float tanimotoSimiarity(double[] features1, double[] features2) {
		int n = features1.length;
		double ab = 0.0;
		double a2 = 0.0;
		double b2 = 0.0;

		for (int i = 0; i < n; i++) {
			ab += features1[i] * features2[i];
			a2 += features1[i]*features1[i];
			b2 += features2[i]*features2[i];
		}
		return (float)ab/(float)(a2+b2-ab);
		//sum(xi,yi)
		//sum(xi * xi)
		//sum(yi * yi)
		//sum(xi,yi)/(sum(xi * xi) + sum(yi * yi) + sum(xi,yi))
	}



	/**
	 * Received two double vectors and calculates the Ellenberg similarity.
	 * From:
	 * 	//https://www.javatips.net/api/libsim-master/java/libsim/src/main/java/com/simmachines/libsim/enc/vector/Ellenberg.java
	 * @param v1 vector number 1.
	 * @param v2 vector number 2.
	 * @return The Ellenberg similarity.
	 */
	public static  double ellenbergSimilarity(double[] v1,double[] v2){
		double sumNum = 0;
		double sumDenom = 0;
		for(int i=0;i<v1.length;i++){
			sumNum += (v1[i] + v2[i])*zeroProduct(v1[i],v2[i],false);
			sumDenom += (v1[i] + v2[i])*(1+zeroProduct(v1[i],v2[i],true));
		}
		return sumNum/sumDenom;
	}

	/**
	 * Received two double vectors and calculates the Gleason similarity.
	 * From:
	 * https:www.javatips.net/api/libsim-master/java/libsim/src/main/java/com/simmachines/libsim/enc/vector/Gleason.java
	 * @param v1 vector number 1.
	 * @param v2 vector number 2.
	 * @return The Gleason similarity.
	 */
	public static  double gleasonSimilarity(double[] v1,double[] v2){

		double sumNum = 0;
		double sumDenom = 0;
		for(int i=0;i<v1.length;i++){
			sumNum += (v1[i] + v2[i])*zeroProduct(v1[i],v2[i],false);
			sumDenom += (v1[i] + v2[i]);
		}
		return sumNum/sumDenom;
	}


	/**
	 * Received two double vectors and calculates the Ruzicka distance.
	 * From:
	 * https://www.javatips.net/api/libsim-master/java/libsim/src/main/java/com/simmachines/libsim/enc/vector/Ruzicka.java
	 * @param v1 vector number 1.
	 * @param v2 vector number 2.
	 * @return The Ruzicka distance.
	 */
	public static  double ruzickaSimilarity(double[] v1,double[] v2){
		double sumMin = 0;
		double sumMax = 0;
		for(int i=0;i<v1.length;i++){
			sumMin += Math.min(v1[i], v2[i]);
			sumMax += Math.max(v1[i], v2[i]);
		}
		return (sumMin/sumMax);
	}


	/**
	 * Received two double vectors and calculates the Bray-Curtis similarity.
	 * From:
	 * https://www.javatips.net/api/libsim-master/java/libsim/src/main/java/com/simmachines/libsim/enc/vector/BrayCurtis.java
	 * @param v1 vector number 1.
	 * @param v2 vector number 2.
	 * @return The Bray-Curtis similarity.
	 */
	public static  double brayCurtisSimilarity(double[] v1,double[] v2){

		double sum = 0;
		double promV1 = 0;
		double promV2 = 0;
		for(int i=0;i<v1.length;i++){
			promV1 += v1[i];
			promV2 += v2[i];
			sum += Math.min(v1[i], v2[i]);
		}
		promV1 /= v1.length;
		promV2 /= v1.length;
		return ((2/(v1.length*(promV1+promV2)))*sum);
	}

	/**
	 * Checks if the product of two doubles is equal to zero, and returns 1 or 0 depending on the wanted result
	 * @param x double number 1
	 * @param y double number 2
	 * @param resultZero boolean that indicates if the zero is the wanted result or if the wanted is a non-zero result
	 * @return if the result you need is zero, then returns 1 if resultZero is true (as long as the product result is zero), 0 if it is false; if the result you need is non-zero, then returns 1 if resultZero is false (as long as the porduct result is non-zero), 0 if it is true. 
	 */
	public static  int zeroProduct(double x, double y, boolean resultZero){
		if(x*y==0){
			if(resultZero){
				return 1;
			}else{
				return 0;
			}
		}else{
			if(resultZero){
				return 0;
			}else{
				return 1;
			}
		}
	}


}
