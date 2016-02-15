module JrJackson
  module BuildInfo
    def self.version
      '0.3.9'
    end

    def self.release_date
      '2016-04-09'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end
  end
end
