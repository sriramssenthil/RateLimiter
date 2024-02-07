package RateLimiter;

public class TokenBucket{
    private final long maxTokens;
    private final long refillRate;
   // private final long refillAmount;
    private long availableTokens;
    private long lastRefill;
    long maxRefillElapsed = 60 * 1_000_000_000L; // 60 seconds in nanoseconds

    public TokenBucket(long maxTokens, long refillRate){
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        //this.refillAmount = refillAmount;
        this.availableTokens = maxTokens;
        this.lastRefill = System.nanoTime();
        this.refill();
    }

    private void refill(){
        long currTime = System.nanoTime();
        long elapsed = currTime - lastRefill;
        
        elapsed = Math.min(elapsed, maxRefillElapsed);

        long refillTokens = (elapsed * refillRate) / 1_000_000_000L; // Convert elapsed time back to seconds for refill calculation
        if (refillTokens > 0) {
            availableTokens = Math.min(maxTokens, availableTokens + refillTokens);
            lastRefill += elapsed; 
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

    public synchronized long getAvailableTokens() {
        refill();
        return availableTokens;
    }

    public static void main(String[] args) throws InterruptedException{
        TokenBucket bucket = new TokenBucket(7, 1); // Small bucket for testing
        // int allowedRequests = 0;
        // for (int i = 0; i < 10; i++) {
        //     if (bucket.checkRequest()) {
        //         allowedRequests++;
        //     }
        // }
        // System.out.println(allowedRequests);

        bucket.checkRequest(); // Consumes 1 token, assuming this request is allowed.
        long tokensBeforeWait = bucket.getAvailableTokens();
        System.out.println("Available tokens before wait: " + tokensBeforeWait);

        Thread.sleep(5000);
        long tokensAfterWait = bucket.getAvailableTokens();
        System.out.println("Available tokens after wait: " + tokensAfterWait);

    }
}