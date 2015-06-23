class DashboardController < ApplicationController
  before_action :authenticate_user!
  def index
    @recently_added_keyboards = KeyboardVariant.all.order(updated_at: :desc).take(10)
    # @region_array = [];
    # get_all_regions_distinct.each do |region|
    #   if !region.iso_region.nil?
    #     data = region.region_name
    #     unless @region_array.include?(data)
    #       @region_array << data
    #     end
    #   end
    # end
    # @region_with_code = []
    #
    # @region_array.each do |region_name|
    #   @region_code = []
    #   get_all_regions_distinct.each do |region|
    #     if !region.iso_region.nil?
    #       if region_name == region.region_name
    #         unless @region_code.include?(region.iso_region)
    #           @region_code << region.iso_region
    #         end
    #       end
    #     end
    #   end
    #   data = {:region_name => region_name, :region_code => @region_code}
    #   @region_with_code << data
    # end

    #@all_regions = KeyboardCountry.all
  end
end
