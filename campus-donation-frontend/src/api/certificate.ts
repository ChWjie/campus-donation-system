import request from './request'

/**
 * 电子证明 API
 */

export const getMyCertificates = () => {
  return request.get('/certificate/my')
}

export const getCertificateByAppointment = (appointmentId: number) => {
  return request.get(`/certificate/appointment/${appointmentId}`)
}

export const generateCertificate = (appointmentId: number) => {
  return request.post(`/certificate/generate/${appointmentId}`)
}

export const downloadCertificate = (id: number) => {
  return request.get(`/certificate/download/${id}`, {
    responseType: 'blob'
  })
}
