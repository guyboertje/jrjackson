module JrJackson
  module BuildInfo
    def self.version
      '0.3.1'
    end

    def self.release_date
      '2015-09-07'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end
  end
end
