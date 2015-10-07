module JrJackson
  module BuildInfo
    def self.version
      '0.3.5'
    end

    def self.release_date
      '2015-10-07'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end
  end
end
