Imports System.Collections.Generic
Imports InsuranceDB.DataAccess

Public Class CountryService

    Public Property MyGetData As IGetData

    Public Function FindById(countryId As Integer) As Country
        Dim dataSet As DataSet = MyGetData.GetOneDataSet("SELECT * FROM tblCountry WHERE id_country = " & countryId)

        If dataSet.Tables(0).Rows.Count <> 1 Then
            Throw New BalticException("No or multiple countries found with id " & countryId)
        End If

        Return ConvertDataRowToCountry(dataSet.Tables(0).Rows(0))
    End Function

    Public Function FindAll() As List(Of Country)
        Dim result As New List(Of Country)
        Dim dataSet As DataSet = MyGetData.GetOneDataSet("SELECT * FROM tblCountry ORDER BY countryname")
        For Each dataRow As DataRow In dataSet.Tables(0).Rows
            result.Add(ConvertDataRowToCountry(dataRow))
        Next
        Return result
    End Function

    Private Function ConvertDataRowToCountry(dataRow As DataRow) As Country
        Dim result As New Country()
        result.Id = CInt(dataRow.Item("id_country"))
        result.Name = CStr(dataRow.Item("countryname"))
        result.Code = CStr(dataRow.Item("countrycode"))
        result.Citizen = CStr(dataRow.Item("citizen"))
        result.IsoCode = CStr(dataRow.Item("isocountrycode"))
        Return result
    End Function

End Class
