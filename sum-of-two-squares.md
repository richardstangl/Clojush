Richard Stangl
file: Clojush/src/clojush/problems/ec_ai_demos/sum_of_two_squares.clj

My problem was fairly simple: take two integers, square each, and return the sum of the two resulting integers. I tried to pick a fairly simple programming problem to make it more likely that a working program would evolve. However, in my admittedly limited experience, choosing a very straightforward problem seemed to work against me. Only two functions are needed--addition and multiplication--but many more fairly useless ones were generated and bloated the possible solutions.

The setup was also fairly similar. The inputs were a series of vectors containing two integers each. I gave it :integer, :exec, :integer_add, and  :integer_mult as starting functions.

I started with a population of 500 and only :integer, :exec, and :boolean. I eventually increased the population to 1000 since it didn't seem to be going anywhere when I ran it.  I also removed :boolean since booleans aren't really needed to solve the problem. In order to hint the program towards the answer I gave it the two specific functions needed (integer_add and integer_mult).

Even with these very specific hints it still took 58 generations and 100,228,447 evaluations to find a successful program--I added it below.

(exec_stackdepth integer_mod integer_yankdup integer_pop integer_add exec_yankdup integer_mod integer_flush integer_dup exec_dup (integer_max in1 exec_pop (exec_yank exec_noop) integer_mult integer_yank) integer_shove integer_dup in2 exec_swap (exec_do*count (exec_swap (integer_add) (exec_rot () (integer_yank integer_yankdup exec_s () (exec_do*range (in2)) ()) ()))) ())

As you can see it looks like there are a lot of superfluous functions that counteract eachother, but it did find a solution in the end.
