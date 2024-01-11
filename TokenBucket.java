package RateLimiter;

public class TokenBucket{
    private final long maxTokens;
    private final long refillRate;
    private final long refillAmount;
    private long availableTokens;
    private long lastRefill;

    public TokenBucket(long maxTokens, long refillRate, long refillAmount){
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.refillAmount = refillAmount;
        this.availableTokens = maxTokens;
        this.lastRefill = System.nanoTime();

    }

    private void refill(){
        long currTime = System.nanoTime();
        long refillRateNano = 1_000_000_000 / refillRate;
        long elapsed = (currTime - lastRefill) / refillRateNano;

        if(elapsed>0){
            availableTokens = Math.min(maxTokens, availableTokens+(elapsed*refillAmount));
            lastRefill = currTime;
        }
    }

    synchronized boolean checkRequest(){
        refill();

        if(availableTokens>0){
            availableTokens--;
            return true;
        } else {
            return false;
        }
    }
}