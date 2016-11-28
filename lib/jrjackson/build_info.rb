module JrJackson
  module BuildInfo
    def self.version
      '0.4.2'
    end

    def self.release_date
      '2016-11-28'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end
  end
end
