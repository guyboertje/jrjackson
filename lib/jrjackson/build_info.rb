module JrJackson
  module BuildInfo
    def self.version
      '0.4.4'
    end

    def self.release_date
      '2017-10-06'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end

    def self.jackson_version
      '2.9.1'
    end

    def self.jar_version
      '1.2.22'
    end
  end
end
