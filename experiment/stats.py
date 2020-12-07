import scipy as sp
import scipy.stats as spstats

def doChiSq(p1, p2, numGames=5000, verbose=False):
    expWins = int(p1*numGames)
    expLosses = numGames - expWins
    obsWins = int(p2*numGames)
    obsLosses = numGames - obsWins
    chisq, p = spstats.chisquare(f_obs=[obsWins, obsLosses], f_exp=[expWins, expLosses])
    
    if (verbose):
        print("================================")
        print("================================")
        print("================================")
        print("Chi-square test scenario")
        print("-------------------------")
    
        print("P1 win rate:", p1)
        print("P2 win rate:", p2)
        print("Each player played", numGames, "games.")
        print("P1 is before the treatment.")
        print("P2 is after the treatment.")
        print("H0: P2's proportion is the same as P1's proportion. So I'll treat P1 as the 'expected' results, and P2 as the 'observed' results.")
        print("Ha: P2's proportion is actually higher than P1's.")
        print("So this is a one-sided test, because Ha is 'higher', not just 'different'.")
        print("-------------------------")
        print("Doing a chi-square test.")
    
        winTerm = (((obsWins - expWins)**2) / expWins)
        lossTerm = (((obsLosses - expLosses)**2) / expLosses)
        chisqBYHAND = winTerm + lossTerm
        print("P1 (expected results) (wins, losses): (", expWins, ", ", expLosses, ")", sep="")
        print("P2 (observed results) (wins, losses): (", obsWins, ", ", obsLosses, ")", sep="")
        print("By hand, chisq statistic:", chisqBYHAND)
    
        print("Built-in function-call (chisq stat, p): (", chisq, ", ", p, ")", sep="")
        print("Since my by-hand result matches the function call result, it looks like I'm calling the function correctly.")
    
        print("-------------------------")
        
        alpha = 0.05
        print("alpha:", alpha)
    
        print("p:", p)
        
        rejectNullP = p < alpha
        print("Checking if ", p, " < ", alpha, sep="")
        print("Can reject null according to p? ", "Yes." if rejectNullP else "No.", sep="")
    
        print("-------------------------")
        
        print("chisq statistic:", chisq)
        
        degreesOfFreedom = 1  # number of outcome classes - 1
        print("There are two possible outcomes (win or lose), so 2-1=1 degree of freedom.")
    
        chisq_crit = sp.stats.chi2.ppf(1-alpha, degreesOfFreedom)
        print("chisq critical value:", chisq_crit)
            
        rejectNullCrit = chisq > chisq_crit
        print("Checking if ", chisq, " > ", chisq_crit, sep="")
        print("So reject null according to critical value? ", "Yes." if rejectNullCrit else "No.", sep="")
        
    return chisq, p

def chi_sq_experiment(win_rate, original_guess):
    print("Show the change from where we started to the last iteration we did")
    print("iteration", 0, "experiment", 5, "vs iteration", 3, "experiment", 5, "has p-value:", doChiSq(original_guess, win_rate[2][4]))
    print("Show the change between consecutive experiments")
    num_itr = len(win_rate)
    num_exp_each_itr = len(win_rate[0])
    #prev_exp_winrate = win_rate[0][0]
    prev_i = 0
    prev_e = 0
    for i in range(num_itr):
        for e in range(num_exp_each_itr):
            if i == 0 and e == 0:
                print("iteration", 0, "experiment", 5, "vs iteration", 1, "experiment", 1, "has p-value:", doChiSq(original_guess, win_rate[0][0]))
                continue
            print("iteration", prev_i + 1, "experiment", prev_e + 1, "vs iteration", i + 1, "experiment", e + 1, "has p-value:", doChiSq(win_rate[prev_i][prev_e], win_rate[i][e]))
            prev_i = i
            prev_e = e
            #prev_exp_winrate = win_rate[i][e]
    print("Show the change between consecutive iterations")
    win_rate_itr = [original_guess, win_rate[0][4], win_rate[1][4], win_rate[2][4]]
    for i in range(len(win_rate_itr) - 1):
        print("iteration", i, "experiment", 5, "vs iteration", i + 1, "experiment", 5, "has p-value:", doChiSq(win_rate_itr[i], win_rate_itr[i+1]))
def main():
    print("Suppose player 1 is before some optimization (e.g. exhaustive search), whereas player 2 is after some optimization.")
    print("Suppose player 2 has a better win rate. So you think player 2 is better. But is that win rate statistically significantly better?")
    print("The function doChiSq returns a p value. Pass it winRate for player 1, winRate for player 2, where player 2 is the one you think is better (the higher win rate).")
    print("Note optional parameters too, if you want. (All tests I ran are 5000 games.)")
    print("p<0.05 means 'statistically significant'.\np<0.02 is a stronger test (more significant).\np<0.01 even stronger.\np<x means (1-x)% confidence that P2 is better than P1.")
    print("Example 1:", doChiSq(0.710142028, 0.739948), "So yes, in this example player 2 is very strongly significantly better than player 1.")
    print("Example 2:", doChiSq(0.710, 0.72259), "So no, in this example player 2 is actually not better than player 1.")
    
    print("------------------------------------Statistics for ensemble -----------------------------------------------")
    winrate_ensemble = [[0.7197, 0.7137, 0.7146, 0.7194, 0.73925], [0.7376, 0.7344, 0.7338, 0.7372, 0.7472], [0.74, 0.7316, 0.7266, 0.7242, 0.736]]
    chi_sq_experiment(winrate_ensemble, 0.7012)
    print("------------------------------------Statistics for index -------------------------------------------------")
    winrate_index = [[0.7314, 0.7242, 0.7428, 0.746, 0.7554], [0.7554, 0.7458, 0.7508, 0.7522, 0.7534], [0.759, 0.7434, 0.7474, 0.751, 0.748]]
    chi_sq_experiment(winrate_index, 0.7146)
    
main()

