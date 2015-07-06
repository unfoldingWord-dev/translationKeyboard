class RegionController < ApplicationController
  include LanguageHelper
  def index
    get_keyboards_for_region(params[:iso_region])
    @region_code = params[:iso_region]
  end
end
