package info.quantlab.numericalmethods.lecture;

import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.model.ProcessModel;

public class Utils {

	/**
	 * Returns the BlackScholesModel 'processModel' used to build
	 * the Monte Carlo simulation, if any.
	 * 
	 * @param model The Monte-Carlo simulation model.
	 * @return The BlackScholesModel (ProcessModel).
	 */
	public static BlackScholesModel getBlackScholesModelFromMonteCarloModel(AssetModelMonteCarloSimulationModel model) {
		ProcessModel processModel;
		if(model instanceof MonteCarloAssetModel) {
			processModel = ((MonteCarloAssetModel)model).getModel();
		}
		else if(model instanceof MonteCarloBlackScholesModel) {
			processModel = ((MonteCarloBlackScholesModel)model).getModel();
		}
		else {
			throw new IllegalArgumentException("Argument type not supported: " + model.getClass());
		}
		
		if(!(processModel instanceof BlackScholesModel)) {
			throw new IllegalArgumentException("Argument contains an unsupproted ProcessModel: " + processModel.getClass());
		}
		
		return (BlackScholesModel)processModel;
	}

}
