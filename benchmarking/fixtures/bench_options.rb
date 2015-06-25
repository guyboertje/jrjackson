module BenchOptions
  SETS = [0, 250, 5]
  CURRENT = 1

  extend self

  def iterations
    SETS[CURRENT]
    # 5000
  end

  def source
    "benchmarking/fixtures/source#{CURRENT}.json"
  end

  def output_width
    44
  end
end
