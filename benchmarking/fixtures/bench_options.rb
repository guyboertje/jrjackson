module BenchOptions
  SETS = [0, 250, 5]
  CURRENT = 1

  extend self

  def iterations
    SETS[CURRENT]
  end

  def source
    "benchmarking/fixtures/source#{CURRENT}.json"
  end
end
