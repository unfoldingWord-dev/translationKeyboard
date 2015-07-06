class Api::V3::BaseController < ApplicationController

  respond_to :json
  before_action :force_json_response

  rescue_from ActiveRecord::RecordNotFound do |exception|
    render json: {success: 'false', message: 'Record not found'}, status: 404
  end

  def force_json_response
    request.format = :json
  end



end
