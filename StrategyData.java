public class StrategyData {
    private String name;
    private String type;
    private String assetClass;
    private String timeframe;
    private double profitTarget;
    private double stopLoss;
    private boolean useTrailingStop;
    private double maxRisk;
    private int maxPositions;
    
    public StrategyData() {
        this.name = "";
        this.type = "Momentum";
        this.assetClass = "Stocks";
        this.timeframe = "1 Day";
        this.profitTarget = 5.0;
        this.stopLoss = 2.0;
        this.useTrailingStop = false;
        this.maxRisk = 2.0;
        this.maxPositions = 5;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getAssetClass() { return assetClass; }
    public void setAssetClass(String assetClass) { this.assetClass = assetClass; }
    
    public String getTimeframe() { return timeframe; }
    public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
    
    public double getProfitTarget() { return profitTarget; }
    public void setProfitTarget(double profitTarget) { this.profitTarget = profitTarget; }
    
    public double getStopLoss() { return stopLoss; }
    public void setStopLoss(double stopLoss) { this.stopLoss = stopLoss; }
    
    public boolean isUseTrailingStop() { return useTrailingStop; }
    public void setUseTrailingStop(boolean useTrailingStop) { this.useTrailingStop = useTrailingStop; }
    
    public double getMaxRisk() { return maxRisk; }
    public void setMaxRisk(double maxRisk) { this.maxRisk = maxRisk; }
    
    public int getMaxPositions() { return maxPositions; }
    public void setMaxPositions(int maxPositions) { this.maxPositions = maxPositions; }
}